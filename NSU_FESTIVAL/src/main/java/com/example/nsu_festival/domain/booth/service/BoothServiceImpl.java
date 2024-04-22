package com.example.nsu_festival.domain.booth.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.nsu_festival.domain.booth.dto.*;
import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.entity.BoothCategory;
import com.example.nsu_festival.domain.booth.entity.Menu;
import com.example.nsu_festival.domain.booth.repository.BoothCategoryRepository;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import com.example.nsu_festival.domain.booth.repository.MenuRepository;
import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.comment.repository.CommentRepository;
import com.example.nsu_festival.domain.likes.repository.BoothLikedRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.exception.CustomException;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.nsu_festival.global.exception.ExceptionCode.SERVER_ERROR;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoothServiceImpl implements BoothService{


    private final BoothRepository boothRepository;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final MenuRepository menuRepository;
    private final BoothCategoryRepository boothCategoryRepository;
    private final AmazonS3 amazonS3;
    private final BoothLikedRepository boothLikedRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    public List<String> getBoothImgList(String directory){
        List<String> fileList = new ArrayList<>();

        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request() // 가져올 파일 목록을 요쳥하기 위한 객체
                .withBucketName(bucketName)  //버킷 이름 지정
                .withPrefix(directory+"/");  //폴더 경로 지정

        ListObjectsV2Result result = amazonS3.listObjectsV2(listObjectsV2Request); // AmazonS3 객체를 사용하여 S3 버킷에서 파일 객체 목록을 가져와 result에 저장
        List<S3ObjectSummary> objectSummaries = result.getObjectSummaries(); // 파일 객체 목록에서 객체 요약 정보를 추출하여 저장

        for (S3ObjectSummary objectSummary : objectSummaries) {
            String key = objectSummary.getKey();// key를 추출 ex) jpg, png
            if (!key.equals(directory + "/")) {
                fileList.add("https://"+bucketName+".s3."+region+".amazonaws.com/" + key);
            }
        }
        return fileList;
    }




    /**
         *
         * 부스리스트 조회
         */
    @Cacheable("booths")
    public List<AllBoothDto> getAllBooths(){
        try{
            List<AllBoothDto> allBoothDtoLists = boothRepository.findAll().stream()
//                    .filter(booth -> booth.getBoothCategories().stream().noneMatch(category->category.getCategory().equals("푸드트럭")))
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return allBoothDtoLists;
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }

    }

    /**
     *
     * 푸드트럭 리스트 조회
     */

    public List<AllBoothDto> getAllFoodTrucks() {
        try {
            List<AllBoothDto> allBooths = boothRepository.findAll().stream().
                    filter(boothDto -> boothDto.getBoothCategories().stream()
                            .anyMatch(category -> category.getCategory().equals("푸드트럭")))
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return allBooths;
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }

    }

    private AllBoothDto convertToDto(Booth booth){
        return modelMapper.map(booth, AllBoothDto.class);
    }

    /**
     *
     * 부스 상세페이지 조회
     */
    public BoothDetailDto getDetailBooth(Long boothId, CustomOAuth2User customOAuth2User, int startIndex, int endIndex) {
        try{

            Booth booth = boothRepository.findById(boothId).get();
            List<Menu> menu = menuRepository.findMenusByBooth(booth);

            List<Comment> comments = commentRepository.findAllCommentByBooth(booth);
            List<Comment> rangedComments = comments.subList(Math.max(0, startIndex-1), Math.min(endIndex-1, comments.size()));
            List<Comment> sortedComments = rangedComments.stream()
                    .sorted(Comparator.comparing(Comment::getCreatAt).reversed())
                    .collect(Collectors.toList());
            Long boothCommentCounts = commentRepository.countCommentByBooth(booth);

            List<BoothCommentDto> commentDtos = new ArrayList<>();


            for (Comment boothComment : sortedComments) {
                User commentUser = boothComment.getUser();

                if (commentUser != null ) {
                    String userName = commentUser.getNickName();
//                    if(userName.equals("클린봇")){
//                        BoothCommentDto commentDto = new BoothCommentDto();
//                        commentDto.setCommentId(boothComment.getCommentId());
//                        commentDto.setContent(boothComment.getContent());
//                        commentDto.setUserName(userName);
//                        commentDtos.add(commentDto);
//                    }
                    if (userName.length() >= 2 && !"클린봇".equals(userName)) {
                        String maskedName = userName.substring(0, 1) + "*" + userName.substring(2);
                        BoothCommentDto commentDto = new BoothCommentDto();
                        commentDto.setCommentId(boothComment.getCommentId());
                        commentDto.setContent(boothComment.getContent());
                        commentDto.setUserName(maskedName);
                        commentDtos.add(commentDto);
                    } else {
                        BoothCommentDto commentDto = new BoothCommentDto();
                        commentDto.setCommentId(boothComment.getCommentId());
                        commentDto.setContent(boothComment.getContent());
                        commentDto.setUserName(userName);
                        commentDtos.add(commentDto);
                    }
                }
            }


            BoothDetailDto boothDetailDto = BoothDetailDto.builder()
                    .boothId(booth.getBoothId())
                    .area(booth.getArea())
                    .title(booth.getTitle())
                    .countLike(booth.getCountLike() > 1000 ? booth.getCountLike()/1000.0 + "k" : String.valueOf(booth.getCountLike()))
                    .content(booth.getContent())
                    .boothImageUrl(booth.getBoothImageUrl())
                    .boothCategories(booth.getBoothCategories())
                    .comments(commentDtos)
                    .boothCommentCount(boothCommentCounts)
                    .menus(menu)
                    .entryFee(booth.getEntryFee())
                    .boothName(booth.getBoothName())
                    .build();
            return boothDetailDto;

        }
        catch (RuntimeException e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }

    }

    @Transactional
    @Override
    public List<TopBoothResponseDto> findTopBooths() {

        List<TopBoothResponseDto> responseList = new ArrayList<>();
        List<Booth> findTopBoothList = boothRepository.findTopBoothByCountLike();
        for (Booth booth : findTopBoothList) {
            String topBoothTitle = "";
            topBoothTitle = verifyDepartment(booth.getTitle());

            if (topBoothTitle.length() >= 6){
                topBoothTitle = addSpacing(topBoothTitle);
            }

            TopBoothResponseDto topBoothResponseDto = TopBoothResponseDto.builder()
                    .boothId(booth.getBoothId())
                    .title(topBoothTitle)
                    .build();
            responseList.add(topBoothResponseDto);
        }
        return responseList;
    }

    public String verifyDepartment(String title){

        if(title.contains("공학과")){
            title = title.substring(0, title.length()-1);
        }

        if (title.length() >= 6 && title.endsWith("학과")) {
            title = title.substring(0, title.length()-2);
        }
        return title;
    }

    public String addSpacing(String title){
        switch (title){
            case "원탑푸드트럭", "마이요거트립" :
                title = title.substring(0, 2) + " " + title.substring(2);
                break;
            case "영상예술디자인", "공간조형디자인" :
                title = title.substring(0, 4) + " " + title.substring(4);
                break;
            case "스포츠비즈니스", "스포츠건강관리" :
                title = title.substring(0, 3) + " " + title.substring(3);
                break;
            case "지능정보통신공학", "빅데이터경영공학", "드론공간정보공학",
                    "대외국제교류처1", "대외국제교류처2", "대외국제교류처3":
                title = title.substring(0, 4) + " " + title.substring(4);
                break;
            case "컴퓨터소프트웨어":
                title = title.substring(0, 3) + " " + title.substring(3, 6) + " " + title.substring(6);
                break;
            case "고깃집-스테이크":
                title = title.replace("-", " ");
                break;
            case "시각미디어디자인":
                title = title.substring(0, 2) + " " + title.substring(2, 5) + " " + title.substring(5);
                break;
            case "바이오헬스컨디셔닝":
                title = title.substring(0, 3) + " " + title.substring(3, 5) + " " + title.substring(5);
                break;
            case "대외혁신지원사업단":
                title = title.substring(0, 4) + " " + title.substring(4);
            default:
                break;
        }
        return title;
    }

    @Override
    public BoothDetailDto getDetailBooth(String boothName) {
        //부스 이름으로 부스 검색
        Optional<Booth> OptionalBooth = boothRepository.findBoothByTitle(boothName);
        //부스 이름으로 찾은 부스가 없다면 예외발생, 존재하면 goekd 부스 상세 데이터 return
        Booth findBooth = OptionalBooth.orElseThrow(() -> new NoSuchElementException());
        return BoothDetailDto.builder()
                .boothId(findBooth.getBoothId())
                .title(findBooth.getTitle())
                .content(findBooth.getContent())
                .area(findBooth.getArea())
                .entryFee(findBooth.getEntryFee())
                .boothName(findBooth.getBoothName())
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = "booths", allEntries = true)
    public void updateBoothDetails(Long boothId,
                                   BoothDetailsRequestDto requestDto) {
        try {
            //부스Id로 해당 부스를 찾아 값 업데이트
            Booth findBooth = boothRepository.findBoothByBoothId(boothId);
            findBooth.updateBoothDetails(requestDto);
            boothRepository.save(findBooth);
        } catch (EntityNotFoundException e) {   //부스ID와 일치하는 값을 찾지 못했을 때 예외처리
            throw new NoSuchElementException();
        } catch (Exception e ){     //이외 상황에 대한 예외처리
            throw new RuntimeException();
        }
    }


    @PostConstruct // 초기 데이터 설정 어노테이션
    public void initializeData() {


        List<String> foodTruckImage = getBoothImgList("foodTruckImage");
        List<String> defaultImages = getBoothImgList("defaultImage");
        String defaultImage = defaultImages.get(0);
        List<String> department = getBoothImgList("departmentBooth");
        List<String> clubImage = getBoothImgList("clubBoothImage");



        // 부스 1
        Booth booth1 = new Booth(1L, "가상현실학과", "vr 게임 및 크로마키 사진 출력", 0, "A-1", department.get(0), null, "가상현실 체험",null, null, null,null);
        List<BoothCategory> categories1 = new ArrayList<>();
        boothRepository.save(booth1);
        categories1.add(new BoothCategory(16L,"게임",booth1));
        categories1.add(new BoothCategory(17L,"체험",booth1));
        categories1.add(new BoothCategory(18L,"학과",booth1));
        boothCategoryRepository.saveAll(categories1);


        // 부스 2
        Booth booth2 = new Booth(2L, "뷰티보건학과", "MAKE UP \n 눈썹정리 : 1,000 \n 가닥속눈썹 : 1,500 \n 하트블러셔 : 1,000 \n 큐빅 : 1,500 \n\n HAIR \n 스타일링(고데기) : 2,000 \n 스타일링(묶음) : 2,000 \n 노쇼 방지를 위해 예약금 1,000원을 미리 받고 있습니다. \n 예약금 입글 계좌 \n 카카오뱅크 3333300480464 박주영 \n 계좌이체만 가능합니다. (현금 불가능) \n 계좌 이름 : 학번 앞자리 + 이름 (ex. 22xxx)"
                , 0, "A-2", department.get(1), null, "뷰티라이트",null,null, null,null);
        List<BoothCategory> categories2 = new ArrayList<>();
        boothRepository.save(booth2);
        categories2.add(new BoothCategory(19L,"체험",booth2));
        categories2.add(new BoothCategory(20L,"학과",booth2));
        boothCategoryRepository.saveAll(categories2);

        // 부스 3
        Booth booth3 = new Booth(3L, "멀티미디어학과", "미션 풍선 다트 / 3,000원 \n - 미션을 뽑아 수행하며 다트를 던져 터트린 풍선 개수마다 상품 증정! \n펀치 / 2,000원 \n - 하루마다 기록을 세우고 최종 순위 1등 ~ 3등까지 상품 증정 (남,여 따로 분류하여 상품증정) \n 멀티 필름 / 2장 500원 \n - 네컷 포토부스에서 사진찍고 잊지 못할 추억 만들기"
                , 0, "A-3", department.get(2),null ,"야! 멀방9",null,null, null, null);
        List<BoothCategory> categories3 = new ArrayList<>();
        boothRepository.save(booth3);
        categories3.add(new BoothCategory(21L,"게임",booth3));
        categories3.add(new BoothCategory(22L,"체험",booth3));
        categories3.add(new BoothCategory(23L,"학과",booth3));
        boothCategoryRepository.saveAll(categories3);

        // 부스 4
        Booth booth4 = new Booth(4L, "빅데이터경영공학과", "여러종류의 보드게임", 0, "A-4", department.get(3), null,"오락가락",null,null, null, null);
        List<BoothCategory> categories4 = new ArrayList<>();
        boothRepository.save(booth4);
        categories4.add(new BoothCategory(24L,"게임",booth4));
        categories4.add(new BoothCategory(25L,"학과",booth4));
        boothCategoryRepository.saveAll(categories4);

        // 부스 5
        Booth booth5 = new Booth(5L, "전자공학과", "여러종류의 보드게임", 0, "A-5", department.get(4),null, "오락가락",null, null, null, null);
        List<BoothCategory> categories5 = new ArrayList<>();
        boothRepository.save(booth5);
        categories5.add(new BoothCategory(26L,"게임",booth5));
        categories5.add(new BoothCategory(27L,"학과",booth5));
        boothCategoryRepository.saveAll(categories5);

        // 부스 6
        Booth booth6 = new Booth(6L, "글로벌무역학과", "카페운영 및 미니게임 뽑기", 0, "A-6", department.get(5),null,"남서울대 갬성카페",null, null,  null, null);
        List<BoothCategory> categories6 = new ArrayList<>();
        boothRepository.save(booth6);
        categories6.add(new BoothCategory(28L,"게임",booth6));
        categories6.add(new BoothCategory(29L,"학과",booth6));

// 부스 7
        Booth booth7 = new Booth(7L, "휴먼케어학과", "사진관 및 거짓말 탐지기", 0, "A-8", department.get(6),null,"휴케사진관",null, null, null, null);
        List<BoothCategory> categories7 = new ArrayList<>();
        boothRepository.save(booth7);
        categories7.add(new BoothCategory(30L,"체험",booth7));
        categories7.add(new BoothCategory(31L,"학과",booth7));
        boothCategoryRepository.saveAll(categories7);

// 부스 8
        Booth booth8 = new Booth(8L, "스포츠비즈니스학과", "물총사격, 번호추첨 로또", 0, "A-7", department.get(7),null,null,null, null,  null, null);
        List<BoothCategory> categories8 = new ArrayList<>();
        boothRepository.save(booth8);
        categories8.add(new BoothCategory(32L,"체험",booth8));
        categories8.add(new BoothCategory(33L,"게임",booth8));
        categories8.add(new BoothCategory(34L,"학과",booth8));
        boothCategoryRepository.saveAll(categories8);

// 부스 9
        Booth booth9 = new Booth(9L, "스마트함학과", "못박기 게임, 뽑기판", 0, "A-9", department.get(8), null,"HAMMER MASTER",null, null, null, null);
        List<BoothCategory> categories9 = new ArrayList<>();
        boothRepository.save(booth9);
        categories9.add(new BoothCategory(35L,"게임",booth9));
        categories9.add(new BoothCategory(36L,"학과",booth9));
        boothCategoryRepository.saveAll(categories9);

// 부스 10
        Booth booth10 = new Booth(10L, "간호학과", "-경품뽑기(당첨-->배달의 민족 상품권,무드등 등)\n-혈액팩 음룍수\n이 두 가지 상품이 단, 1,000원?!!!\n이 외에도 학생회를 이겨라 등의 게임이 준비 되어있으니\n많은 관심 부탁드립니다:)\n(**부스를 운영하면서 발생한 모든 수익은 건강취약계층을 위해 기부할 예정입니다)", 0, "A-10", department.get(9), null,"뭐라구?! 간호학과의 1,000원의 행복?!",null,null, null, null);
        List<BoothCategory> categories10 = new ArrayList<>();
        boothRepository.save(booth10);
        categories10.add(new BoothCategory(37L,"학과",booth10));
        boothCategoryRepository.saveAll(categories10);

        // 부스 11
        Booth booth11 = new Booth(11L, "아동복지학과", "다들 봄을 맞이해 운영을 찾고 계시지 않으신가요? 그런 분들을 위한 이미지 번호팅 \n 번호팅을 통해 여러분들의 운명의 상대를 쟁취하세요", 0, "A-11", department.get(10),"3,000","너 나랑 일촌맺자.",null, null, null, null);
        List<BoothCategory> categories11 = new ArrayList<>();
        boothRepository.save(booth11);
        categories11.add(new BoothCategory(38L,"게임",booth11));
        categories11.add(new BoothCategory(39L,"체험",booth11));
        categories11.add(new BoothCategory(40L,"학과",booth11));
        boothCategoryRepository.saveAll(categories11);

// 부스 12
        Booth booth12 = new Booth(12L, "시각디자인학과", "꼬치의 달인 보드게임, 폴라로이드, 타투스티커", 0, "A-12", department.get(11),null, "시디포차",null, null, null, null);
        List<BoothCategory> categories12 = new ArrayList<>();
        boothRepository.save(booth12);
        categories12.add(new BoothCategory(41L,"게임",booth12));
        categories12.add(new BoothCategory(42L,"체험",booth12));
        categories12.add(new BoothCategory(43L,"학과",booth12));
        boothCategoryRepository.saveAll(categories12);

// 부스 13
        Booth booth13 = new Booth(13L, "건축공학과", "딱지치기, 물건쌓기게임, 코끼리 코 돌고 상품뽑기", 0, "A-13", department.get(12),null, "반짝반짝 건공 문방구에 인생 걸어볼래?",null,null,null, null);
        List<BoothCategory> categories13 = new ArrayList<>();
        boothRepository.save(booth13);
        categories13.add(new BoothCategory(44L,"게임",booth13));
        categories13.add(new BoothCategory(45L,"학과",booth13));
        boothCategoryRepository.saveAll(categories13);

// 부스 14
        Booth booth14 = new Booth(14L, "보건행정학과", "인스타팅 \n - 응모/뽑기 비용 : 1,000원 \n - 응모 및 뽑기 횟수 제한 없습니다. \n - 인스타 계정 공개로 전환 필수입니다. \n 과일 화채 & 폴라로이드 \n 화채 2,500원 \n 폴라로이드 1장 1,500원 \n 스티커사진(흑백) 1장당 1,000원 \n\n 세트할인 \n - SET \n 1. 화채 + 폴라로이드 = 3,500원 / 2. 화채 + 스티커사진 = 3,000원 \n\n 못 박기 게임 / 부스 비용 : 2,000원 \n - 못 1개를 주어진 횟수 안에 박으면 성공 \n -남자 3번/ 여자 6번"

                , 0, "A-14", department.get(13),"1,000 ~ 3,500", "보행랜드",null, null, null, null);
        List<BoothCategory> categories14 = new ArrayList<>();
        boothRepository.save(booth14);
        categories14.add(new BoothCategory(46L,"소개팅",booth14));
        categories14.add(new BoothCategory(47L,"체험",booth14));
        categories14.add(new BoothCategory(48L,"학과",booth14));
        boothCategoryRepository.saveAll(categories14);

// 부스 15
        Booth booth15 = new Booth(15L, "세무학과", "사격 및 뽑기", 0, "A-15", department.get(14),null,"전투사격/랜덤뽑기",null, null, null, null);
        List<BoothCategory> categories15 = new ArrayList<>();
        boothRepository.save(booth15);
        categories15.add(new BoothCategory(49L,"게임",booth15));
        categories15.add(new BoothCategory(50L,"학과",booth15));
        boothCategoryRepository.saveAll(categories15);

        // 부스 16
        Booth booth16 = new Booth(16L, "경영학과", "게임 및 만들기 체험", 0, "A-16",department.get(15), null,"공작소파트너",null,null, null, null);
        List<BoothCategory> categories16 = new ArrayList<>();
        boothRepository.save(booth16);
        categories16.add(new BoothCategory(51L,"게임",booth16));
        categories16.add(new BoothCategory(52L,"학과",booth16));
        boothCategoryRepository.saveAll(categories16);

// 부스 17
        Booth booth17 = new Booth(17L, "공간조형디자인학과", "도자컵 핸드페인팅, 경품뽑기, 유리악세사리판매", 0, "A-17", department.get(16),null,"레드클렌, 작은모임, 인글라스",null, null,  null, null);
        List<BoothCategory> categories17 = new ArrayList<>();
        boothRepository.save(booth17);
        categories17.add(new BoothCategory(53L,"체험",booth17));
        categories17.add(new BoothCategory(54L,"학과",booth17));
        boothCategoryRepository.saveAll(categories17);

// 부스 18
        Booth booth18 = new Booth(18L, "지능정보통신공학과", "야구공 던져서 원기둥 맞히기, 탱탱볼 대야에 넣기, 비비탄 사격, 뽑기판", 0, "A-18", department.get(17), null,"해변가 오락실",null,null,null, null);
        List<BoothCategory> categories18 = new ArrayList<>();
        boothRepository.save(booth18);
        categories18.add(new BoothCategory(55L,"게임",booth18));
        categories18.add(new BoothCategory(56L,"학과",booth18));
        boothCategoryRepository.saveAll(categories18);

// 부스 19
        Booth booth19 = new Booth(19L, "물리치료학과", "원하는 공 선택 후 랜덤 도구로 공 튕기기 \n 1번 연습 후 2번의 기회 중 가장 많이 튕긴 횟수 기록 \n\n 게임 종류 및 비용 \n 1) 균형 잡으면서 링 던지기 \n 1회 참여 - 2,000원 \n 2회 참여 - 3,000원 \n 2) 랜덤 도구로 공 튕기기 \n 1회 참여 - 1,000원 \n 2회 참여 - 1,500원 \n\n 상품 \n 1등 : 배민 상품권 2만원 \n 2등 : 공차 상품권 1만원 \n 등 : GS25 상품권 5천원 \n 참가상 : 젤리 또는 야광팔찌"
                , 0, "A-19", department.get(18),"1,000 ~ 3,000","물치랑 링팅통",null, null, null, null);
        List<BoothCategory> categories19 = new ArrayList<>();
        boothRepository.save(booth19);
        categories19.add(new BoothCategory(57L,"게임",booth19));
        categories19.add(new BoothCategory(58L,"학과",booth19));
        boothCategoryRepository.saveAll(categories19);

// 부스 20
        Booth booth20 = new Booth(20L, "일어일문학과", "만들기 체험 \n 여우가면 만들기 - 3,000원 \n 후우링 꾸미기 - 4,000원 \n 만들기 체험을 하신 분들에게 일본식 '빙수'를 증정해 드립니다. \n 여우가면 완성품 구매 - 3,000원 \n\n 뽑기 체험 \n 가라퐁 뽑기 1회 - 2,000원 \n 가라퐁 뽑기 3회 \n - 5,000원 \n\n 경품 \n 1등 : 쿠로미 스피커 \n 2등 : 짱구 텀블러 \n 3등 : 메타몽 인형 \n 4등 : 브레댄코 5천원 쿠폰 \n 5등 : 우마이봉 2개"
                , 0, "A-20", department.get(19),"2,000 ~ 5,000","일본놀이 체험부스",null,null, null,  null);
        List<BoothCategory> categories20 = new ArrayList<>();
        boothRepository.save(booth20);
        categories20.add(new BoothCategory(59L,"체험",booth20));
        categories20.add(new BoothCategory(60L,"학과",booth20));
        boothCategoryRepository.saveAll(categories20);

        // 부스 21
        Booth booth21 = new Booth(21L, "부동산학과", "달고나뽑기", 0, "A-21", department.get(18),null,"달고나게임",null,  null, null, null);
        List<BoothCategory> categories21 = new ArrayList<>();
        boothRepository.save(booth21);
        categories21.add(new BoothCategory(61L,"게임",booth21));
        categories21.add(new BoothCategory(62L,"학과",booth21));
        boothCategoryRepository.saveAll(categories21);


// 부스 22
        Booth booth22 = new Booth(22L, "글로벌한국학과", "동물머리핀 판매, 공가 컵 놀이", 0, "A-22", department.get(21),null,"공과 컵 놀이",null, null, null, null);
        List<BoothCategory> categories22 = new ArrayList<>();
        boothRepository.save(booth22);
        categories22.add(new BoothCategory(63L,"게임",booth22));
        categories22.add(new BoothCategory(64L,"학과",booth22));
        boothCategoryRepository.saveAll(categories22);

// 부스 23
        Booth booth23 = new Booth(23L, "사회복지학과", "물풍선, 폴라로이드", 0, "A-23", department.get(22), null,"뿅뿅지구게임실",null,null, null, null);

        List<BoothCategory> categories23 = new ArrayList<>();
        boothRepository.save(booth23);
        categories23.add(new BoothCategory(65L,"게임",booth23));
        categories23.add(new BoothCategory(66L,"학과",booth23));
        boothCategoryRepository.saveAll(categories23);

// 부스 24
        Booth booth24 = new Booth(24L, "드론공간정보공학과", "뿅망치게임, 정신력으로 슬리퍼넣기, 랜덤뽑기, 야광팔찌 판매", 0, "A-24", department.get(23),null,"뿅뿅지구게임실",null, null, null, null);
        List<BoothCategory> categories24 = new ArrayList<>();
        boothRepository.save(booth24);
        categories24.add(new BoothCategory(67L,"체험",booth24));
        categories24.add(new BoothCategory(68L,"학과",booth24));
        boothCategoryRepository.saveAll(categories24);

// 부스 25
        Booth booth25 = new Booth(25L, "건축학과", "페이스페인팅 + 타투스티커, 추억의게임(신서유기)", 0, "A-25", department.get(24), null,"페이스페인팅",null,null, null, null);
        List<BoothCategory> categories25 = new ArrayList<>();
        boothRepository.save(booth25);
        categories25.add(new BoothCategory(69L,"게임",booth25));
        categories25.add(new BoothCategory(70L,"체험",booth25));
        categories25.add(new BoothCategory(71L,"학과",booth25));
        boothCategoryRepository.saveAll(categories25);

        // 부스 26
        Booth booth26 = new Booth(26L, "임상병리학과", "영수증사진기로 사진 및 꾸미기", 0, "A-26", department.get(25),null,"임병사진관",null, null, null, null);
        List<BoothCategory> categories26 = new ArrayList<>();
        boothRepository.save(booth26);
        categories26.add(new BoothCategory(72L,"체험",booth26));
        categories26.add(new BoothCategory(73L,"학과",booth26));
        boothCategoryRepository.saveAll(categories26);

// 부스 27
        Booth booth27 = new Booth(27L, "광고홍보학과", "테무깡(뽑기)", 0, "A-27", department.get(26),null,"운수좋은날",null, null, null, null);
        List<BoothCategory> categories27 = new ArrayList<>();
        boothRepository.save(booth27);
        categories27.add(new BoothCategory(74L,"게임",booth27));
        categories27.add(new BoothCategory(75L,"학과",booth27));
        boothCategoryRepository.saveAll(categories27);

// 부스 28
        Booth booth28 = new Booth(28L, "실용음악과", "쪽지미션게임", 0, "A-28", department.get(27),null,"미니게임천국",null, null, null, null);
        List<BoothCategory> categories28 = new ArrayList<>();
        boothRepository.save(booth28);
        categories28.add(new BoothCategory(76L,"게임",booth28));
        categories28.add(new BoothCategory(77L,"학과",booth28));
        boothCategoryRepository.saveAll(categories28);

// 부스 29
        Booth booth29 = new Booth(29L, "호텔경영학과", "미니게임 + 논알콕캌테일 시음", 0, "A-29", department.get(28),null,"사복과 워터밤",null, null, null, null);
        List<BoothCategory> categories29 = new ArrayList<>();
        boothRepository.save(booth29);
        categories29.add(new BoothCategory(78L,"체험",booth29));
        categories29.add(new BoothCategory(79L,"학과",booth29));
        boothCategoryRepository.saveAll(categories29);

// 부스 30
        Booth booth30 = new Booth(30L, "중국학과", "가위야바위야보 \n LV1 = 1회 성공(스피드게임 1회 무료) \n LV2 = 2회 성공(스피드게임 1회 무로 + 행운부적)\n LV3 = 3회 성공(브래드앤코 5천원 쿠폰) \n 참가비 : 2,000원 \n\n 스피드 게임 \n 4가지 미니게임들을 가장 빠른 시간내에 성공하세요! \n 축제기간 3일중 가장빠른 시간내에 미션을 완수한 분께 10만원 상당의 헤드셋 상품을 지급해 드립니다! \n 참가비 : 1,000원 \n\n 폴라로이드 \n 1년중 단 한번 축제의 추억을 사진으로 간직하세요! \n 참가비 : 한장당 1,500원 \n\n 판매상품 \n 야광팔찌 : 500원"
                , 0, "A-30", department.get(29),"500 ~ 2,000","중국아~놀자~",null, null, null, null);
        List<BoothCategory> categories30 = new ArrayList<>();
        boothRepository.save(booth30);
        categories30.add(new BoothCategory(80L,"체험",booth30));
        categories30.add(new BoothCategory(81L,"게임",booth30));
        categories30.add(new BoothCategory(82L,"학과",booth30));
        boothCategoryRepository.saveAll(categories30);

        // 부스 31
        Booth booth31 = new Booth(31L, "바이오헬스컨디셔닝학과", "첫번째는 스포츠 마사지입니다! \n 꽁꽁 얼어붙은 승모 위로 거북이가 걸어다닙니다.\n 비용 : 3분에 3천원! \n 인스타팅 \n\n 현장접수 : 1,000원\n기다림은 싫어! 고민보단 GO! \n 상대방의 인스타그램 아이디 뽑기! (2,000원) \n\n 시간팅 \n 접수 : 2,000원 \n\n 상품안내 \n 치킨 한마리, 올영 상품권 1만원, 메가커피 5천원권, 야광팔찌 "
                , 0, "A-31", department.get(30),"1,000 ~ 3,000","님 승모근 팡!팡!팡!",null, null, null, null);
        List<BoothCategory> categories31 = new ArrayList<>();
        boothRepository.save(booth31);
        categories31.add(new BoothCategory(83L,"체험",booth31));
        categories31.add(new BoothCategory(84L,"소개팅",booth31));
        categories31.add(new BoothCategory(85L,"학과",booth31));
        boothCategoryRepository.saveAll(categories31);

// 부스 32
        Booth booth32 = new Booth(32L, "유통마케팅학과", "학생회 임원이 직접 만들어주는 솜사탕 \n - 딸기맛 \n - 오렌지 맛 \n - 멜론 맛  \n - 포도 맛", 0, "A-32", department.get(31), "1,500 ~ 2,000","ak47맞고 대박난 솜사탕집",null,null,  null, null);
        List<BoothCategory> categories32 = new ArrayList<>();
        boothRepository.save(booth32);
        categories32.add(new BoothCategory(86L,"게임",booth32));
        categories32.add(new BoothCategory(87L,"학과",booth32));
        boothCategoryRepository.saveAll(categories32);

// 부스 33
        Booth booth33 = new Booth(33L, "관광경영학과", "서울 - 대전 - 순천 - 여수로 떠나는 기차 영해 각 지역에 맞는 게임을 진행 \n\n -참가비용 2,500원 \n 폴라로이드 장당 1,500원 별도 \n\n 참가상 오리지널티겟 증정 ", 0, "A-33", department.get(32),"1,500 ~ 2,500","관경철도 999",null, null,  null, null);
        List<BoothCategory> categories33 = new ArrayList<>();
        boothRepository.save(booth33);
        categories33.add(new BoothCategory(88L,"게임",booth33));
        categories33.add(new BoothCategory(89L,"학과",booth33));
        boothCategoryRepository.saveAll(categories33);

// 부스 34
        Booth booth34 = new Booth(34L, "컴퓨터소프트웨어학과", "사격왕\n1회 3,000원 / 2회 5,000원 \n 학생회비 납부자 할인 - 2회 3,000원 \n\n 타자왕\n 1회 3,000원 \n 2회 5,000원 \n 학생회비 납부자 할인 - 2회 3,000원 \n\n 사격왕, 타자왕 상품 \n1등: 배달의 민족 5천원권 \n2등: 무드등 \n3등: 대형 엔터키 쿠션\n참가상품: 야광팔찌\n\n 추가상품 응원봉\n 사격왕 - 50점 이상\n타자왕 - 130타 이상", 0, "A-34", department.get(33), null,"사격왕, 타자왕",null,null,  null, null);
        List<BoothCategory> categories34 = new ArrayList<>();
        boothRepository.save(booth34);
        categories34.add(new BoothCategory(90L,"게임",booth34));
        categories34.add(new BoothCategory(91L,"학과",booth34));
        boothCategoryRepository.saveAll(categories34);

// 부스 35
        Booth booth35 = new Booth(35L, "스포츠건강관리학과", "참가비 \n 물풍선 1개 - 1,000원 \n 물풍선 2개 - 2,000원 \n 물풍선 6 개 - 3,000원 \n\n 30초 푸쉬업 \n 일시 : 4월 25 (목) 11:00 - 17:00  단 하루 \n 참가비 : 3,000원 \n (스포츠 관련과 4,000원) \n\n 1등 상금 : 15만원 (남,여 따로) \n 2등 상금 : 10만원 (남,여 따로) \n 3등 상금 : 5만원 (남,여 따로) ", 0, "A-35", department.get(34),"1,000 ~ 4,000","welcome to amazon season2",null, null, null, null);
        List<BoothCategory> categories35 = new ArrayList<>();
        boothRepository.save(booth35);
        categories35.add(new BoothCategory(92L,"게임",booth35));
        categories35.add(new BoothCategory(93L,"체험",booth35));
        categories35.add(new BoothCategory(94L,"학과",booth35));
        boothCategoryRepository.saveAll(categories35);

        // 부스 36
        Booth booth36 = new Booth(36L, "아메리타트", "책상에 놓여진 풀백 카트를 당겨 최대한 멀리 보내는 게임을 진행한다. \n 가장 멀리 보내는 사람에게 소정의 상품을 증정. \n 또한 게임을 이용한 고객에게는 야광팔찌를 증정한다.\n 상품 : 소정의 상품/ 참가상" ,
                 0, "B-1", clubImage.get(0),null,"아메리카트",  null,  null, null, null);
        List<BoothCategory> categories36 = new ArrayList<>();
        boothRepository.save(booth36);
        categories36.add(new BoothCategory(95L,"게임",booth36));
        categories36.add(new BoothCategory(96L,"체험",booth36));
        categories36.add(new BoothCategory(97L,"동아리",booth36));
        boothCategoryRepository.saveAll(categories36);

// 부스 37
        Booth booth37 = new Booth(37L, "굿네이버스", "번호팅 - 본인이 원하는 MBTI 박스에서 이성의 번호를 뽑아 연락\n 베프팅 - 본인이 원하는 유형의 박스에서 동성의 번호를 뽑아 연락\n 4/25(목) 번호팅/베프팅으로 만난 사람과 함께 부스에 방문해 게임을 진행 -> 점수가 가장 높은 번호팅 한 팀, 베프팅 여자 한 팀, 베프팅 남자 한 팀을 선정해 경품 지급\n 이외에도 비눗방을 & 야광 팔찌 판매 \n 상품 \n cgv 콤보 & 인생네컷 상품권 / 야광봉" ,
                 0, "B-2", clubImage.get(1), "1,000","번호팅, 베프팅",null, null, null, null);
        List<BoothCategory> categories37 = new ArrayList<>();
        boothRepository.save(booth37);
        categories37.add(new BoothCategory(98L,"소개팅",booth37));
        categories37.add(new BoothCategory(99L,"체험",booth37));
        categories37.add(new BoothCategory(100L,"동아리",booth37));
        boothCategoryRepository.saveAll(categories37);

// 부스 38
        Booth booth38 = new Booth(38L, "아랑", "축제가 시작하기전 미리 여러 가지 질문들을 통해 나에 대한 간단한 소개를 하며 자신의 답변이 담긴 종이를 남 여 성별 따로 따로 바구니에 넣습니다. \n\n 천막 뒤에는 할리갈리 등등 여러 가지 보드게임을 준비해서 보드게임과 음료를 마시며 축제를 즐깁니다.", 0, "B-3", clubImage.get(2), null,"WE LIKE 2 PARTY",null,null, null, null);
        List<BoothCategory> categories38 = new ArrayList<>();
        boothRepository.save(booth38);
        categories38.add(new BoothCategory(101L,"게임",booth38));
        categories38.add(new BoothCategory(102L,"체험",booth38));
        categories38.add(new BoothCategory(103L,"동아리",booth38));
        boothCategoryRepository.saveAll(categories38);

        // 부스 39
        Booth booth39 = new Booth(39L, "KOZ", "풍선 터뜨리기 \n - 8개 이상 배달의 민족 5,000원권 \n -5개 이상 1,000원 상당의 상품 \n -5개 이하 야간봉\n, 주짓수 자세 맞추기 \n - 8개 이상 배달의 민족 5,000원권 \n -5개 이상 1,000원 상당의 상품 \n 5개 이하 야간봉 "
                , 0, "B-4", clubImage.get(3), null,"koz의 놀이터",null, null, null, null);
        List<BoothCategory> categories39 = new ArrayList<>();
        boothRepository.save(booth39);
        categories39.add(new BoothCategory(104L,"게임",booth39));
        categories39.add(new BoothCategory(105L,"체험",booth39));
        categories39.add(new BoothCategory(106L,"동아리",booth39));
        boothCategoryRepository.saveAll(categories39);

        // 부스 40
        Booth booth40 = new Booth(40L, "NSTV", "1. 야광 팔찌, 야광 탱탱볼, 야광 별자리판 중 만들고 싶은 것 하나를 정한다 \n 2. 한가지가 정해지면 자리를 내부로 들어온다.\n 3. 학우들의 선택에 맞게 상품을 만들도록 한다. \n 4. 만든 상품을 들고, 사진 촬영을 선택하도록 한다."
                , 0, "B-5", clubImage.get(4),null,"반짝반짝 NSTV",null,  null, null, null);
        List<BoothCategory> categories40 = new ArrayList<>();
        boothRepository.save(booth40);
        categories40.add(new BoothCategory(107L,"게임",booth40));
        categories40.add(new BoothCategory(108L,"체험",booth40));
        categories40.add(new BoothCategory(109L,"동아리",booth40));
        boothCategoryRepository.saveAll(categories40);

        // 부스 41
        Booth booth41 = new Booth(41L, "로타랙트", "풍선 터뜨리기는 한 사람당 2,000원의 금액을 내면 10번을 쏠 수 있게 진행하려 합니다. \n 뽑기는 1,000원의 금액을 내면 상품을 드리는 방식으로 진행 방식을 계획하고 있습니다.",
                0, "B-6", clubImage.get(5),"1,000 ~ 2,000","풍선 터트리고 상품받자",null,  null, null, null);
        List<BoothCategory> categories41 = new ArrayList<>();
        boothRepository.save(booth41);
        categories41.add(new BoothCategory(110L,"게임",booth41));
        categories41.add(new BoothCategory(111L,"체험",booth41));
        categories41.add(new BoothCategory(112L,"동아리",booth41));
        boothCategoryRepository.saveAll(categories41);

        // 부스 42
        Booth booth42 = new Booth(42L, "IVF", "4가지 미니게임을 준비(병뚜껑 멀리 날리기, 참참참, 딱지치기, 물병세우기) \n\n 1. 랜덤으로 미니게임 정하기 \n 2. 4가지 미니게임 중 한 개 진행 \n 3. 게임 이길 시 뽑기판에서 뽑기 진행 후 상품 수령  \n\n (꽝없음, 이길 때까지 게임) \n 상품 : 팝콘, 기프티콘, 폴라로이드 사진 등."
                , 0, "B-7", clubImage.get(6), null,"아벱이를 이겨라",null, null, null, null);
        List<BoothCategory> categories42 = new ArrayList<>();
        boothRepository.save(booth42);
        categories42.add(new BoothCategory(113L,"게임",booth42));
        categories42.add(new BoothCategory(114L,"체험",booth42));
        categories42.add(new BoothCategory(115L,"동아리",booth42));
        boothCategoryRepository.saveAll(categories42);

// 부스 43
        Booth booth43 = new Booth(43L, "노스텔지어", "각목에 박힌 못을 망치로 쳐서 각목에 못 박기 게임 \n 여자 남자 모두 동일하게 기회 5번씩 기회 안에 성공시 상품 \n 성공 - 상품증정 / 참가상 야광팔찌"
                , 0, "B-8", clubImage.get(7), null,"누가누가 힘이 넘치나~",null,null, null, null);
        List<BoothCategory> categories43 = new ArrayList<>();
        boothRepository.save(booth43);
        categories43.add(new BoothCategory(116L,"게임",booth43));
        categories43.add(new BoothCategory(117L,"체험",booth43));
        categories43.add(new BoothCategory(118L,"동아리",booth43));
        boothCategoryRepository.saveAll(categories43);

// 부스 44
        Booth booth44 = new Booth(44L, "RCY", "1. 사랑의 쉼터 운영 : 간단한 먹을거리와 의자, 보드게임 등을 준비하여 학우들에게 따뜻한 공간을 마련한 부스 \n\n 2. 봉사(RCY) 관련 및 넌센스 등의 퀴즈 맞추기 \n 간단한 넌센스 퀴즈를 준비하여 맞추는 학우들에게는 추첨을 통해 5,000원 이하의 상품 \n 참여한 학우들에게 간단한 간식을 제공 ",
                0, "B-9", clubImage.get(8), null,"사랑의 쉼터에서 퀴즈를 맞춰라!",null,null,  null, null);
        List<BoothCategory> categories44 = new ArrayList<>();
        boothRepository.save(booth44);
        categories44.add(new BoothCategory(119L,"게임",booth44));
        categories44.add(new BoothCategory(120L,"체험",booth44));
        categories44.add(new BoothCategory(121L,"동아리",booth44));
        boothCategoryRepository.saveAll(categories44);

// 부스 45
        Booth booth45 = new Booth(45L, "NVP", "\n 미니게임 1) 배구공을 공바구니에 언더리시브로 넣기. (성공 개수에 따라 상품 차등 지급) \n 2) 주사위를 던져 테이블에 지정된 구역을 선정후 병뚜껑을 손가락으로 튕겨 지정된 구역까지 근접시키기(성공시 상품 지급) \n 퀴즈 뽑기 3) 동아리 'NVP'와 관련된 퀴즈와 배구에 관련된 퀴즈가 적혀있는 통에서 퀴즈를 뽑아 퀴즈 맞추기. \n (정답을 맞추면 상품을 지급)"
                , 0, "B-10", clubImage.get(9), null,"NVP와 함께 배구에 빠져보자!",null,null,  null, null);
        List<BoothCategory> categories45 = new ArrayList<>();
        boothRepository.save(booth45);
        categories45.add(new BoothCategory(122L,"게임",booth45));
        categories45.add(new BoothCategory(123L,"체험",booth45));
        categories45.add(new BoothCategory(124L,"동아리",booth45));
        boothCategoryRepository.saveAll(categories45);


// 부스 46
        Booth booth46 = new Booth(46L, "저스트댄스", "1회에 코인 5개를 주고 바둑판 형식의 숫자판을 바닥에 놓고 기준선에서 코인을 던져 해당되는 칸의 숫자만큼 코인을 더 지급한다. \n 성공 개수에 따라 상품 차등 지급",
                0, "B-11", clubImage.get(10), null,"Just coin!",null,null,  null, null);
        List<BoothCategory> categories46 = new ArrayList<>();
        boothRepository.save(booth46);
        categories46.add(new BoothCategory(125L,"게임",booth46));
        categories46.add(new BoothCategory(126L,"체험",booth46));
        categories46.add(new BoothCategory(127L,"동아리",booth46));
        boothCategoryRepository.saveAll(categories46);

// 부스 47
        Booth booth47 = new Booth(47L, "LEAF", "3가지 게임 진행 \n1. 환경 관련 퀴즈 뽑기 \n2. 쓰레기 사진을 보고 올바른 분류 통으로 탁구공을 넣는 재활용품 핑퐁. \n3. 해양쓰레기 관련 피해 원인과 피해 받은 연관 동물카드뒤집기 게임을 진행. \n 3개의 게임 성공 여부에 따라 상품 뽑기 기회 차등 지급",
                0, "B-12", clubImage.get(11), null,"뿅뿅 리프오락실",null,null,  null, null);
        List<BoothCategory> categories47 = new ArrayList<>();
        boothRepository.save(booth47);
        categories47.add(new BoothCategory(128L,"게임",booth47));
        categories47.add(new BoothCategory(129L,"체험",booth47));
        categories47.add(new BoothCategory(130L,"동아리",booth47));
        boothCategoryRepository.saveAll(categories47);

        // 부스 48
        Booth booth48 = new Booth(48L, "토독토독", "1일차 부스 / 책 제목 이어말하기 퀴즈 \n- 출제자가 책 제목의 앞 부분을 말하면 도전자가 뒤의 부분을 제한 시간 내에 맞히는 게임 \n\n 2일차 / 책 속의 숨겨진 문장 찾기 \n- 제한 시간 내에 책 속에 숨겨진 글자를 찾아 문장을 완성하는 게임\n 3일차 / 1일차, 2일차 부스 같이 진행 \n 3문제 성공시, 솜사탕 만들기 체험과 토독토독 자체 제작 책갈피 증정 \n 실패 시, 솜사탕 만들기 체험만 진행"
                , 0, "B-13", clubImage.get(12),null,"솜사탕 청춘 문고",null, null,  null, null);
        List<BoothCategory> categories48 = new ArrayList<>();
        boothRepository.save(booth48);
        categories48.add(new BoothCategory(131L,"게임",booth48));
        categories48.add(new BoothCategory(132L,"체험",booth48));
        categories48.add(new BoothCategory(133L,"동아리",booth48));
        boothCategoryRepository.saveAll(categories48);

// 부스 49
        Booth booth49 = new Booth(49L, "창틀", "1. 젓가락을 과녁에 맞추기 \n2. 정해진 시간을 스톱워치를 눌러 정확하게 맞추는 게임 \n3. 상품이 걸린 뽑기", 0, "B-14", clubImage.get(13), null,"맞춰보시덩가~",null,null, null, null);
        List<BoothCategory> categories49 = new ArrayList<>();
        boothRepository.save(booth49);
        categories49.add(new BoothCategory(134L,"게임",booth49));
        categories49.add(new BoothCategory(135L,"동아리",booth49));
        boothCategoryRepository.saveAll(categories49);

// 부스 50
        Booth booth50 = new Booth(50L, "애드밴처", "제한시간 15분 안에 현상 수배 포스터에 있는 임원들의 특징을 보고 \n축제장 곳곳에 있는 4명의 임원들을 찾아낸다. \n임원을 찾으면 임원이 자신을 찾았다는 증거 스티커를 나눠준다.\n스티커의 개수에 따라 경품 증정(스티커 2개면 경품 2개)"
                , 0, "B-15", clubImage.get(14), null,null,null, null, null, null);
        List<BoothCategory> categories50 = new ArrayList<>();
        boothRepository.save(booth50);
        categories50.add(new BoothCategory(136L,"게임",booth50));
        categories50.add(new BoothCategory(137L,"체험",booth50));
        categories50.add(new BoothCategory(138L,"동아리",booth50));
        boothCategoryRepository.saveAll(categories50);

// 부스 51
        Booth booth51 = new Booth(51L, "새마을", "99초 안에 4가지의 게임을 진행 \n1. 랜덤퀴즈 - 각 퀴즈 분야가 적혀있는 종이를 뽑은 뒤 해당분야의 퀴즈 한 문제를 맞춘다. \n2. 고리던지기 - 고리를 3개 던져서 막대에 끼운다. \n신발양궁 - 과녁판에 신발을 벗어 던져 '10점'을 맞춘다. \n4. 물병던지기 - 물병을 던져 세운다. \n\n 성공한 시간을 순위로 매긴 후 상품을 증정한다.."
                , 0, "B-16", clubImage.get(15), "1,000","미니 운동회 - 한우를 잡아라!",null,null,  null, null);
        List<BoothCategory> categories51 = new ArrayList<>();
        boothRepository.save(booth51);
        categories51.add(new BoothCategory(139L,"게임",booth51));
        categories51.add(new BoothCategory(140L,"체험",booth51));
        categories51.add(new BoothCategory(141L,"동아리",booth51));
        boothCategoryRepository.saveAll(categories51);

// 부스 52
        Booth booth52 = new Booth(52L, "센스(SENS)", "1. 정해진 라인에서 공을 던진다. \n2. 던진 공의 구속을 측정한다. \n3. 나온 구속의 순서대로 상품을 증정한다. \n1등 치킨 교환권/ 2등 커피 1만원권/ 3등 편의점 5천원 상품권\n참가상 - 구속 남자 100km 이상 , 여자 75km 이상 야광봉, 이외 야광팔찌"
                , 0, "B-17", clubImage.get(16), null,"도전! 오타니!",null
                ,null,  null, null);
        List<BoothCategory> categories52 = new ArrayList<>();
        boothRepository.save(booth52);
        categories52.add(new BoothCategory(142L,"게임",booth52));
        categories52.add(new BoothCategory(143L,"체험",booth52));
        categories52.add(new BoothCategory(144L,"동아리",booth52));
        boothCategoryRepository.saveAll(categories52);

// 부스 53
        Booth booth53 = new Booth(53L, "아름다운 사람들", "아사를 찾아서: 상, 중, 하 난이도를 선택후 도장을 받으신 후 \n동아리 부원이 착용한 악세사리를 찾으면 사진 촬영후 부스를 재방문 해주시면 상품을 드립니다. \n\n미니게임 천국~!: 여러 미니게임모음 종이를 뽑아 게임에 맞는 퀴즈를 진행 후 결과에 따른 상품 지급"
                , 0, "B-18", clubImage.get(17), null,"아사랑 놀자~!",null,null,  null, null);
        List<BoothCategory> categories53 = new ArrayList<>();
        boothRepository.save(booth53);
        categories53.add(new BoothCategory(145L,"게임",booth53));
        categories53.add(new BoothCategory(146L,"체험",booth53));
        categories53.add(new BoothCategory(147L,"동아리",booth53));
        boothCategoryRepository.saveAll(categories53);

        // 부스 54
        Booth booth54 = new Booth(54L, "SHOUT", "1. 물풍선 던져서 받기 \n- 5번 2,000원 \n- 8번 3,000원 \n2. 공 던져서 받기\n- 5번 1,000원 \n- 2023년과 동일 (5번 1,000원) \n3. 야광팔찌, 머리핀(파트,오리) 판매 \n- 1개 500원 \n- 3개 1,000원 \n-10개 3,000원 \n상품\n* 공 넣고 돈 먹기 \n- 1등 : 5개 골인 -> 1만원 상당의 상품 \n- 2등 : 4개 골인 -> 7~8천원 상당의 상품 \n-3등 : 3개 골인 -> 3천원 상당의 상품 \n* 물풍선 던져서 받기 \n- 1등 : 5개 골인 -> 5천원 상당의 상품 \n- 2등 : 4개 골인 -> 3천원 상당의 상품 \n- 3등 : 3개 골인 -> 1천원 상당의 상품\n* 꽝 상품 \n- 머리핀(하트,오리), 마이쭈, 야꽝팔찌 1개 中 택 1 \n(물풍선, 공 게임 상품은 기프티콘 or 완제품으로 할 예정)"
                , 0, "B-19", clubImage.get(18), "2,000~3,0000","꽝 없는 게임 한 판 어던데?",null,null,  null, null);
        List<BoothCategory> categories54 = new ArrayList<>();
        boothRepository.save(booth54);
        categories54.add(new BoothCategory(148L,"게임",booth54));
        categories54.add(new BoothCategory(149L,"체험",booth54));
        categories54.add(new BoothCategory(150L,"동아리",booth54));
        boothCategoryRepository.saveAll(categories54);

// 부스 55
        Booth booth55 = new Booth(55L, "싸커 클럽", "1.테니스 공으로 리프팅 \n\n리프팅 3개 이상 뽑기 1번 \n리프팅 6개 이상 뽑기 2번 \n리프팅 9개 이상 뽑기 3번 \n\n2.번호팅 \n\n뽑기 통에서 번호를 뽑고 연락합니다. 1등 배민 3만원 \n2등 배민 2만원 \n3등 배민 1만원 \n4등 배민 5천원 \n5등 불량 식품!"
                , 0, "B-20", clubImage.get(19), "1,000 ~ 2,000","팅팅~!",null
                ,null, null,  null);
        List<BoothCategory> categories55 = new ArrayList<>();
        boothRepository.save(booth55);
        categories55.add(new BoothCategory(151L,"게임",booth55));
        categories55.add(new BoothCategory(152L,"체험",booth55));
        categories55.add(new BoothCategory(153L,"동아리",booth55));
        boothCategoryRepository.saveAll(categories55);

// 부스 56
        Booth booth56 = new Booth(56L, "제너시스", "1. 가라폰 돌리기 \n나오는 구슬의 색에 따라 상품이 결정됩니다. \n\n2. 슬리퍼 던지기 \n슬리퍼를 과녁에 던져 해당 점수의 상품을 가져가는 방식 \n\n3. 물병 맞추기 \n책상 위 세워져 있는 물병을 맞추어 맞추는 개수에 따라 상품을 가져가는 방식"
                , 0, "B-21", clubImage.get(20), "2,000","하나비 마켓",null,null, null, null);
        List<BoothCategory> categories56 = new ArrayList<>();
        boothRepository.save(booth56);
        categories56.add(new BoothCategory(154L,"게임",booth56));
        categories56.add(new BoothCategory(155L,"체험",booth56));
        categories56.add(new BoothCategory(156L,"동아리",booth56));
        boothCategoryRepository.saveAll(categories56);

        // 부스 57
        Booth booth57 = new Booth(57L, "케렌시아", "1.폴라로이드\n부스 앞 쪽에는 폴라로이드로 사진촬영 진행합니다. \n2.게임 2종 세트 \n컬링과 하키 게임을 진행합니다. 총 3판을 진행하며 2판을 먼저 승리하신 분께 간식 상품을 드리고자 제공.\n3.뽑기의 신\n1부터 5까지 적힌 종이를 하나 뽑고 숫자를 확인한 후 해당 숫자에 맞는 상품을 제공. \n1번 - 원하는 브랜드 2만원권 기프티콘 \n2번 - 브레덴코 만 원권 쿠폰 \n3번 - LED 새싹머리띠 한 개 + 폴라로이드 한 장 \n4번 - 한 번 더 \n5번 - 꽝"
                , 0, "B-22", clubImage.get(21),null,"케렌시아 대축제",null, null,  null, null);
        List<BoothCategory> categories57 = new ArrayList<>();
        boothRepository.save(booth57);
        categories57.add(new BoothCategory(157L,"게임",booth57));
        categories57.add(new BoothCategory(158L,"체험",booth57));
        categories57.add(new BoothCategory(159L,"동아리",booth57));
        boothCategoryRepository.saveAll(categories57);
// 부스 58
        Booth booth58 = new Booth(58L, "대학혁신지원사업단", "등록된 내용이 없습니다.", 0, "B-23", clubImage.get(22),null,null,null, null,  null, null);
        List<BoothCategory> categories58 = new ArrayList<>();
        boothRepository.save(booth58);
        categories58.add(new BoothCategory(160L,"동아리",booth58));
        boothCategoryRepository.saveAll(categories58);

// 부스 59
        Booth booth59 = new Booth(59L, "애니지크", "PS4와 닌텐도 스위치로 진행자를 이기시면 됩니다. \n 상품 : 야광 팔찌"
                , 0, "B-24", clubImage.get(23),null,"고수를 이겨라!",null, null,  null, null);
        List<BoothCategory> categories59 = new ArrayList<>();
        boothRepository.save(booth59);
        categories59.add(new BoothCategory(161L,"게임",booth59));
        categories59.add(new BoothCategory(162L,"체험",booth59));
        categories59.add(new BoothCategory(163L,"동아리",booth59));
        boothCategoryRepository.saveAll(categories59);

        // 부스 60
        Booth booth60 = new Booth(60L, "시네마 떼끄", "영화의 하이라이트 장면을 확인 후 다음 장면의 대사 혹은 \n등장인물을 맞추면 뽑기를 진행", 0, "B-25", clubImage.get(24),null,"남서울 영화제",null, null, null,  null);
        List<BoothCategory> categories60 = new ArrayList<>();
        boothRepository.save(booth60);
        categories60.add(new BoothCategory(164L,"게임",booth60));
        categories60.add(new BoothCategory(165L,"체험",booth60));
        categories60.add(new BoothCategory(166L,"동아리",booth60));
        boothCategoryRepository.saveAll(categories60);

// 부스 61
        Booth booth61 = new Booth(61L, "소리터", "미니게임 \n1일차 : 10초 맞추기 \n2일차 : 한 호흡 챌린지 \n3일차 : 10초 맞추기, 한 호흡 챌린지 \n\n산리오 타투샵 : 작은거 2개 1,000원 / 큰거 1개 2,000원 \n꽃삔 : 1개 500원 \n3개 : 1,000원 \n\n부스 참여시 랜덤 뽑기 무료로 진행 \n\n 랜덤 뽑기 상품 \n야광 팔찌, 야광 곱창 머리끈, 야광머리띠, 비눗방울, 추억의 간식 등 \n당일 1등에게 상품 지급(배민 기프티콘 2만원)", 0, "B-26", clubImage.get(25),"500 ~ 2,000","미니게임 타투샵. 꽃핀",null, null, null, null);
        List<BoothCategory> categories61 = new ArrayList<>();
        boothRepository.save(booth61);
        categories61.add(new BoothCategory(167L,"게임",booth61));
        categories61.add(new BoothCategory(168L,"체험",booth61));
        categories61.add(new BoothCategory(169L,"체험",booth61));
        boothCategoryRepository.saveAll(categories61);

// 부스 62
        Booth booth62 = new Booth(62L, "Scon(스콘)", "1. 다른 두가지 노래를 동시에 재상하여 두가지 노래를 맞추기 \n2. 신청공을 선정해서 축제당일 크게 듣고싶은 노래를 틀어 드립니다. \n사진 요청 시 동아리 장비로 축제의 추억을 남길 수 있는 사진 촬영 \n\n1등 : 상품 치킨 상품권 기프티콘, 2등 : 러비더비 섬유향수 \n3등 : 키링(다양한 종류), 4등 : 야광팔찌", 0, "B-27", clubImage.get(26),null,"Scon",null, null, null,  null);
        List<BoothCategory> categories62 = new ArrayList<>();
        boothRepository.save(booth62);
        categories62.add(new BoothCategory(170L,"게임",booth62));
        categories62.add(new BoothCategory(171L,"체험",booth62));
        categories62.add(new BoothCategory(172L,"동아리",booth62));
        boothCategoryRepository.saveAll(categories62);

        // 부스 63
        Booth booth63 = new Booth(63L, "아우트런스", "1일차 \n- 5M 거리에서 장난감 공으로 바구니 크기별 한 번씩 던져 넣기 \n2일차 \n- 옛날 문방구 뽑기 및 손가락 농구공 미니 게임기 점수 채우기", 0, "B-28", clubImage.get(27), null,"농구 어디까지 해봤니",null,null, null,  null);
        List<BoothCategory> categories63 = new ArrayList<>();
        boothRepository.save(booth63);
        categories63.add(new BoothCategory(173L,"게임",booth63));
        categories63.add(new BoothCategory(174L,"체험",booth63));
        categories63.add(new BoothCategory(175L,"동아리",booth63));
        boothCategoryRepository.saveAll(categories63);


// 부스 64
        Booth booth64 = new Booth(64L, "C.C.C", "1. 딱지 4개를 6번의 기회 안에 뒷면으로 뒤집는다. \n2. 뒤집기 성공 시 뽑기 기회가 주어진다. \n1등 치킨쿠폰 1명(파루 1명, 총 3명) \n2등 베스킨라빈스 파인트 쿠폰2명 (하루 2명, 총 6명) \n3등 CU 5천원권 5명(하루 5명, 총 15명) \n4등 메가커피 아이스아메리카노 1잔 20명 (하루 20명, 총 60명)\n5등 L홀더 30명(소진 시 까지)\n6등 꽝", 0, "B-29", clubImage.get(28), null,"딱지왕을 찾아라!",null,null,  null, null);
        List<BoothCategory> categories64 = new ArrayList<>();
        boothRepository.save(booth64);
        categories64.add(new BoothCategory(176L,"게임",booth64));
        categories64.add(new BoothCategory(177L,"체험",booth64));
        categories64.add(new BoothCategory(178L,"동아리",booth64));
        boothCategoryRepository.saveAll(categories64);

// 부스 65
        Booth booth65 = new Booth(65L, "취창업지원처", "등록된 내용이 없습니다.", 0, "B-30", clubImage.get(29), null,null,null, null, null, null);
        List<BoothCategory> categories65 = new ArrayList<>();
        boothRepository.save(booth65);
        categories65.add(new BoothCategory(179L,"동아리",booth65));
        boothCategoryRepository.saveAll(categories65);

// 부스 66
        Booth booth66 = new Booth(66L, "하나은행", "등록된 내용이 없습니다.", 0, "B-31", clubImage.get(30), null,null,null,null,  null, null);
        List<BoothCategory> categories66 = new ArrayList<>();
        boothRepository.save(booth66);
        categories66.add(new BoothCategory(180L,"동아리",booth66));
        boothCategoryRepository.saveAll(categories66);
// 부스 67
        Booth booth67 = new Booth(67L, "대회협력처A", "등록된 내용이 없습니다.", 0, "B-32", clubImage.get(31), null,null,null, null, null, null);
        List<BoothCategory> categories67 = new ArrayList<>();
        boothRepository.save(booth67);
        categories67.add(new BoothCategory(181L,"동아리",booth67));
        boothCategoryRepository.saveAll(categories67);

// 부스 68
        Booth booth68 = new Booth(68L, "대회협력처B", "등록된 내용이 없습니다.", 0, "B-33", clubImage.get(32), null,null,null, null, null, null);
        List<BoothCategory> categories68 = new ArrayList<>();
        boothRepository.save(booth68);
        categories68.add(new BoothCategory(182L,"동아리",booth68));
        boothCategoryRepository.saveAll(categories68);

        // 부스 69
        Booth booth69 = new Booth(69L, "대회협력처C", "등록된 내용이 없습니다.", 0, "a-69", clubImage.get(33), null,null,null, null, null, null);
        List<BoothCategory> categories69 = new ArrayList<>();
        boothRepository.save(booth69);
        categories69.add(new BoothCategory(183L,"동아리",booth69));
        boothCategoryRepository.saveAll(categories69);

// 푸드트럭 1

        Booth foodTruck1 = new Booth(70L,"고깃집-스테이크",null,0,null,getBoothImgList("foodTruckImage").get(5),null,null,null,null,null,null);
        BoothCategory boothCategory1 = new BoothCategory(1L,"먹거리",foodTruck1);
        boothRepository.save(foodTruck1);
        boothCategoryRepository.save(boothCategory1);
        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu(1L,"스테이크+밥","11,000",foodTruck1));
        menus.add(new Menu(2L,"스테이크+소시지","11,000",foodTruck1));
        menus.add(new Menu(3L,"스테이크+감자","10,000",foodTruck1));
        menuRepository.saveAll(menus);
        foodTruck1.setMenus(menus);

// 푸드트럭2
        Booth foodTruck2 = new Booth(71L,"곱창야시장",null,0,null,getBoothImgList("foodTruckImage").get(1),null,null,null,null,null,null);
        BoothCategory boothCategory2 = new BoothCategory(2L,"먹거리",foodTruck2);
        boothRepository.save(foodTruck2);
        boothCategoryRepository.save(boothCategory2);
        List<Menu> menus1 = new ArrayList<>();
        menus1.add(new Menu(4L,"곱창순대볶음(중)","15,000",foodTruck2));
        menus1.add(new Menu(5L,"곱창순대볶음(대)","22,000",foodTruck2));
        menuRepository.saveAll(menus1);
        foodTruck2.setMenus(menus1);

// 푸드트럭3
        Booth foodTruck3 = new Booth(72L,"다온푸드",null,0,null,getBoothImgList("foodTruckImage").get(3),null,null,null,null,null,null);
        BoothCategory boothCategory3 = new BoothCategory(3L,"먹거리",foodTruck3);
        boothRepository.save(foodTruck3);
        boothCategoryRepository.save(boothCategory3);
        List<Menu> menus2 = new ArrayList<>();
        menus2.add(new Menu(6L,"크림,칠리새우(중)","10,000",foodTruck3));
        menus2.add(new Menu(7L,"크림,칠리새우(대)","15,000",foodTruck3));
        menuRepository.saveAll(menus2);
        foodTruck3.setMenus(menus2);

        // 푸드트럭4

        Booth foodTruck4 = new Booth(73L,"대감님댁",null,0,null,getBoothImgList("foodTruckImage").get(0),null,null,null,null,null,null);

        BoothCategory boothCategory4 = new BoothCategory(4L,"먹거리",foodTruck4);
        boothRepository.save(foodTruck4);
        boothCategoryRepository.save(boothCategory4);
        List<Menu> menus3 = new ArrayList<>();
        menus3.add(new Menu(8L,"닭강정(중)","10,000",foodTruck4));
        menus3.add(new Menu(9L,"닭강정(대)","15,000",foodTruck4));
        menuRepository.saveAll(menus3);
        foodTruck4.setMenus(menus3);

        // 푸드트럭5
        Booth foodTruck5 = new Booth(74L,"마이요거트립",null,0,null,getBoothImgList("foodTruckImage").get(4),null,null,null,null,null,null);
        BoothCategory boothCategory5 = new BoothCategory(5L,"먹거리",foodTruck5);
        boothRepository.save(foodTruck5);
        boothCategoryRepository.save(boothCategory5);
        List<Menu> menus4 = new ArrayList<>();
        menus4.add(new Menu(10L,"돼지갈비후라이드(중)","10,000",foodTruck5));
        menus4.add(new Menu(11L,"돼지갈비후라이드(대)","14,000",foodTruck5));
        menus4.add(new Menu(12L,"돼지갈비퀘사디아","8,000",foodTruck5));
        menuRepository.saveAll(menus4);
        foodTruck5.setMenus(menus4);

        // 푸드트럭6
        Booth foodTruck6 = new Booth(75L,"바바파파",null,0,null,getBoothImgList("foodTruckImage").get(12),null,null,null,null,null,null);

        BoothCategory boothCategory6 = new BoothCategory(6L,"먹거리",foodTruck6);
        boothRepository.save(foodTruck6);
        boothCategoryRepository.save(boothCategory6);
        List<Menu> menus5 = new ArrayList<>();
        menus5.add(new Menu(13L,"타코야끼10알","6,000",foodTruck6));
        menuRepository.saveAll(menus5);
        foodTruck6.setMenus(menus5);

        // 푸드트럭7
        Booth foodTruck7 = new Booth(76L,"부엉이푸드",null,0,null,getBoothImgList("foodTruckImage").get(9),null,null,null,null,null,null);

        BoothCategory boothCategory7 = new BoothCategory(7L,"먹거리",foodTruck7);
        boothRepository.save(foodTruck7);
        boothCategoryRepository.save(boothCategory7);
        List<Menu> menus6 = new ArrayList<>();
        menus6.add(new Menu(14L,"닭꼬치","5,000",foodTruck7));
        menuRepository.saveAll(menus6);
        foodTruck7.setMenus(menus6);

        // 푸드트럭8
        Booth foodTruck8 = new Booth(77L,"썬플라워",null,0,null,getBoothImgList("foodTruckImage").get(14),null,null,null,null,null,null);

        BoothCategory boothCategory8 = new BoothCategory(8L,"먹거리",foodTruck8);
        boothRepository.save(foodTruck8);
        boothCategoryRepository.save(boothCategory8);
        List<Menu> menus7 = new ArrayList<>();
        menus7.add(new Menu(15L,"회오리감자","4,000",foodTruck8));
        menus7.add(new Menu(16L,"소떡소떡","3,500",foodTruck8));
        menus7.add(new Menu(17L,"옛날 핫도그","4,000",foodTruck8));
        menus7.add(new Menu(18L,"회오리 핫도그","5,000",foodTruck8));
        menuRepository.saveAll(menus7);
        foodTruck8.setMenus(menus7);

        // 푸드트럭9

        Booth foodTruck9 = new Booth(78L,"어더아사",null,0,null,getBoothImgList("foodTruckImage").get(2),null,null,null,null,null,null);

        BoothCategory boothCategory9 = new BoothCategory(9L,"먹거리",foodTruck9);
        boothRepository.save(foodTruck9);
        boothCategoryRepository.save(boothCategory9);
        List<Menu> menus8 = new ArrayList<>();
        menus8.add(new Menu(19L,"구슬아이스크림 싱글","3,500",foodTruck9));
        menus8.add(new Menu(20L,"구슬아이스크림 더블","7,000",foodTruck9));
        menus8.add(new Menu(21L,"구슬아이스크림 쿼터","9,000",foodTruck9));
        menus8.add(new Menu(22L,"구슬아이스크림 올","11,000",foodTruck9));
        menuRepository.saveAll(menus8);
        foodTruck9.setMenus(menus8);

        // 푸드트럭10
        Booth foodTruck10 = new Booth(79L,"엉클",null,0,null,getBoothImgList("foodTruckImage").get(8),null,null,null,null,null,null);

        BoothCategory boothCategory10 = new BoothCategory(10L,"먹거리",foodTruck10);
        boothRepository.save(foodTruck10);
        boothCategoryRepository.save(boothCategory10);
        List<Menu> menus9 = new ArrayList<>();
        menus9.add(new Menu(23L,"오레오츄러스","4,000",foodTruck10));
        menus9.add(new Menu(24L,"소프트아이스크림","4,000",foodTruck10));
        menus9.add(new Menu(25L,"오레오아츄","6,000",foodTruck10));
        menuRepository.saveAll(menus9);
        foodTruck10.setMenus(menus9);

        // 푸드트럭11
        Booth foodTruck11 = new Booth(80L,"에페스케밥",null,0,null,getBoothImgList("foodTruckImage").get(11),null,null,null,null,null,null);
        BoothCategory boothCategory11 = new BoothCategory(11L,"먹거리",foodTruck11);
        boothRepository.save(foodTruck11);
        boothCategoryRepository.save(boothCategory11);
        List<Menu> menus10 = new ArrayList<>();
        menus10.add(new Menu(26L,"케밥","7,000 ~ 9,000",foodTruck11));
        menus10.add(new Menu(27L,"터키아이스크림","4,000",foodTruck11));
        menuRepository.saveAll(menus10);
        foodTruck11.setMenus(menus10);

        // 푸드트럭12
        Booth foodTruck12 = new Booth(81L,"오션푸드",null,0,null,getBoothImgList("foodTruckImage").get(6),null,null,null,null,null,null);
        BoothCategory boothCategory12 = new BoothCategory(12L,"먹거리",foodTruck12);
        boothRepository.save(foodTruck12);
        boothCategoryRepository.save(boothCategory12);
        List<Menu> menus11 = new ArrayList<>();
        menus11.add(new Menu(28L,"슬러시","3,500",foodTruck12));
        menus11.add(new Menu(29L,"캐릭터슬러시","6,000",foodTruck12));
        menuRepository.saveAll(menus11);
        foodTruck12.setMenus(menus11);

        // 푸드트럭13
        Booth foodTruck13 = new Booth(82L,"오션푸드2",null,0,null,getBoothImgList("foodTruckImage").get(7),null,null,null,null,null,null);

        BoothCategory boothCategory13 = new BoothCategory(13L,"먹거리",foodTruck13);
        boothRepository.save(foodTruck13);
        boothCategoryRepository.save(boothCategory13);
        List<Menu> menus12 = new ArrayList<>();
        menus12.add(new Menu(30L,"야끼소바","10,000",foodTruck13));
        menus12.add(new Menu(31L,"오코노미야끼","10,000",foodTruck13));
        menuRepository.saveAll(menus12);
        foodTruck13.setMenus(menus12);

        // 푸드트럭14

        Booth foodTruck14 = new Booth(83L,"요기분식",null,0,null,getBoothImgList("foodTruckImage").get(10),null,null,null,null,null,null);

        BoothCategory boothCategory14 = new BoothCategory(14L,"먹거리",foodTruck14);
        boothRepository.save(foodTruck14);
        boothCategoryRepository.save(boothCategory14);
        List<Menu> menus13 = new ArrayList<>();
        menus13.add(new Menu(32L,"떡볶이","5,000",foodTruck14));
        menus13.add(new Menu(33L,"순대","5,000",foodTruck14));
        menus13.add(new Menu(34L,"튀김","5,000",foodTruck14));
        menuRepository.saveAll(menus13);
        foodTruck14.setMenus(menus13);

        // 푸드트럭15

        Booth foodTruck15 = new Booth(84L,"원탑푸드트럭",null,0,null,getBoothImgList("foodTruckImage").get(13),null,null,null,null,null,null);
        BoothCategory boothCategory15 = new BoothCategory(15L,"먹거리",foodTruck15);
        boothRepository.save(foodTruck15);
        boothCategoryRepository.save(boothCategory15);
        List<Menu> menus14 = new ArrayList<>();
        menus14.add(new Menu(35L,"불초밥 10p","11,000",foodTruck15));
        menus14.add(new Menu(36L,"연어초밥 10p","12,000",foodTruck15));
        menuRepository.saveAll(menus14);
        foodTruck15.setMenus(menus14);

        initializeLike();

    }

    private void initializeLike(){
        try {
            List<Booth> boothList = boothRepository.findAll();

            for (Booth booth : boothList) {
                if (boothLikedRepository.existsByBooth(booth)) {
                    int count = boothLikedRepository.countBoothLike(booth.getBoothId());
                    booth.updateCountLike(count);
                }
            }
        } catch (RuntimeException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
