package com.example.nsu_festival.domain.booth.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.nsu_festival.domain.booth.dto.BoothCommentDto;
import com.example.nsu_festival.domain.booth.dto.BoothDetailDto;
import com.example.nsu_festival.domain.booth.dto.AllBoothDto;
import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.entity.BoothCategory;
import com.example.nsu_festival.domain.booth.entity.Menu;
import com.example.nsu_festival.domain.booth.repository.BoothCategoryRepository;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import com.example.nsu_festival.domain.booth.repository.MenuRepository;
import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.comment.repository.CommentRepository;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.example.nsu_festival.domain.likes.repository.BoothLikedRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.exception.CustomException;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.nsu_festival.global.exception.ExceptionCode.SERVER_ERROR;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoothServiceImpl implements BoothService{


    private final BoothRepository boothRepository;
   private final ModelMapper modelMapper;
   private final UserRepository userRepository;
   private final BoothLikedRepository boothLikedRepository;
   private final CommentRepository commentRepository;
    private final MenuRepository menuRepository;
    private final BoothCategoryRepository boothCategoryRepository;
    private final AmazonS3 amazonS3;

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

            BoothLiked boothLiked = null;
            if (customOAuth2User != null) {
                User user = userRepository.findByEmail(customOAuth2User.getEmail()).orElseThrow(() -> new CustomException(SERVER_ERROR));
                boothLiked = boothLikedRepository.findBoothLikedByBoothAndUser(booth,user);
                if (boothLiked == null) {

                    boothLiked = BoothLiked.builder()
                            .user(user)
                            .isBoothLike(false)
                            .booth(booth)
                            .build();
                    boothLikedRepository.save(boothLiked);
                }
            } else {
                // 로그인되어 있지 않은 경우
                boothLiked = new BoothLiked();
                boothLiked.updateBoothLiked(false);
            }

            List<Comment> comments = commentRepository.findAllCommentByBooth(booth);
            List<Comment> rangedComments = comments.subList(Math.max(0, startIndex-1), Math.min(endIndex-1, comments.size()));
            List<Comment> sortedComments = rangedComments.stream()
                    .sorted(Comparator.comparing(Comment::getCreatAt).reversed())
                    .collect(Collectors.toList());
            Long boothCommentCounts = commentRepository.countCommentByBooth(booth);

            List<BoothCommentDto> commentDtos = new ArrayList<>();

            for (Comment boothComment : sortedComments) {
                User commentUser = boothComment.getUser();
                if (commentUser != null) {
                    String userName = commentUser.getNickName();
                    if (userName.length() >= 2) {
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
                    .countLike(booth.getCountLike())
                    .content(booth.getContent())
                    .boothImageUrl(booth.getBoothImageUrl())
                    .boothCategories(booth.getBoothCategories())
                    .boothLiked(boothLiked)
                    .comments(commentDtos)
                    .boothCommentCount(boothCommentCounts)
                    .menus(menu)
                    .entryFee(booth.getEntryFee())
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
    public List<AllBoothDto> findTopBooths() {

        List<AllBoothDto> responseList = new ArrayList<>();
        List<Booth> findTopBoothList = boothRepository.findTopBoothByCountLike();
        for (Booth booth : findTopBoothList) {
            AllBoothDto allBoothDto = AllBoothDto.builder()
                    .title(booth.getTitle())
                    .build();
            responseList.add(allBoothDto);
        }
        return responseList;
    }

    @PostConstruct // 초기 데이터 설정 어노테이션
    public void initializeData() {


        List<String> foodTruckImage = getBoothImgList("foodTruckImage");

        // 부스 1
        Booth booth1 = new Booth(1L, "가상현실학과", "내용1", 0, "a-1", "이미지URL1", 0, null, null, null,null);
//        BoothCategory boothCategory1 = new BoothCategory(1L, "게임", booth1);
        boothRepository.save(booth1);
//        booth1.setBoothCategories(Arrays.asList(boothCategory1));
//        boothCategoryRepository.save(boothCategory1);

        // 부스 2
        Booth booth2 = new Booth(2L, "뷰티보건학과", "내용2", 0, "a-2", "이미지URL2", 0, null, null, null,null);
//        BoothCategory boothCategory2 = new BoothCategory(2L, "카테고리2", booth2);
        boothRepository.save(booth2);
//        booth2.setBoothCategories(Arrays.asList(boothCategory2));
//        boothCategoryRepository.save(boothCategory2);

        // 부스 3
        Booth booth3 = new Booth(3L, "멀티미디어학과", "내용3", 0, "a-3", "이미지URL3",0 ,null, null, null, null);
//        BoothCategory boothCategory3 = new BoothCategory(3L, "카테고리3", booth3);
        boothRepository.save(booth3);
//        booth3.setBoothCategories(Arrays.asList(boothCategory3));
//        boothCategoryRepository.save(boothCategory3);

        // 부스 4
        Booth booth4 = new Booth(4L, "유통마케팅학과", "내용4", 0, "a-4", "이미지URL4", 0,null, null, null, null);
//        BoothCategory boothCategory4 = new BoothCategory(4L, "카테고리4", booth4);
        boothRepository.save(booth4);
//        booth4.setBoothCategories(Arrays.asList(boothCategory4));
//        boothCategoryRepository.save(boothCategory4);

        // 부스 5
        Booth booth5 = new Booth(5L, "글로벌무역학과", "내용5", 0, "a-5", "이미지URL5",0, null, null, null, null);
//        BoothCategory boothCategory5 = new BoothCategory(5L, "카테고리5", booth5);
        boothRepository.save(booth5);
//        booth5.setBoothCategories(Arrays.asList(boothCategory5));
//        boothCategoryRepository.save(boothCategory5);

        // 부스 6
        Booth booth6 = new Booth(6L, "영상예술디자인학과", "내용6", 0, "a-6", "이미지URL6",0, null, null, null, null);
//        BoothCategory boothCategory6 = new BoothCategory(6L, "카테고리6", booth6);
        boothRepository.save(booth6);
//        booth6.setBoothCategories(Arrays.asList(boothCategory6));
//        boothCategoryRepository.save(boothCategory6);

// 부스 7
        Booth booth7 = new Booth(7L, "컴퓨터소프트웨어학과", "내용7", 0, "a-7", "이미지URL7",0, null, null, null, null);
//        BoothCategory boothCategory7 = new BoothCategory(7L, "카테고리7", booth7);
        boothRepository.save(booth7);
//        booth7.setBoothCategories(Arrays.asList(boothCategory7));
//        boothCategoryRepository.save(boothCategory7);

// 부스 8
        Booth booth8 = new Booth(8L, "스포츠비즈니스학과", "내용8", 0, "a-8", "이미지URL8",0, null, null, null, null);
//        BoothCategory boothCategory8 = new BoothCategory(8L, "카테고리8", booth8);
        boothRepository.save(booth8);
//        booth8.setBoothCategories(Arrays.asList(boothCategory8));
//        boothCategoryRepository.save(boothCategory8);

// 부스 9
        Booth booth9 = new Booth(9L, "물리치료학과", "내용9", 0, "a-9", "이미지URL9", 0,null, null, null, null);
//        BoothCategory boothCategory9 = new BoothCategory(9L, "카테고리9", booth9);
        boothRepository.save(booth9);
//        booth9.setBoothCategories(Arrays.asList(boothCategory9));
//        boothCategoryRepository.save(boothCategory9);

// 부스 10
        Booth booth10 = new Booth(10L, "전자공학과", "내용10", 0, "a-10", "이미지URL10", 0,null, null, null, null);
//        BoothCategory boothCategory10 = new BoothCategory(10L, "카테고리10", booth10);
        boothRepository.save(booth10);
//        booth10.setBoothCategories(Arrays.asList(boothCategory10));
//        boothCategoryRepository.save(boothCategory10);

        // 부스 11
        Booth booth11 = new Booth(11L, "아동복지학과", "내용11", 0, "a-11", "이미지URL11",0, null, null, null, null);
//        BoothCategory boothCategory11 = new BoothCategory(11L, "카테고리11", booth11);
        boothRepository.save(booth11);
//        booth11.setBoothCategories(Arrays.asList(boothCategory11));
//        boothCategoryRepository.save(boothCategory11);

// 부스 12
        Booth booth12 = new Booth(12L, "실용음악학과", "내용12", 0, "a-12", "이미지URL12",0, null, null, null, null);
//        BoothCategory boothCategory12 = new BoothCategory(12L, "카테고리12", booth12);
        boothRepository.save(booth12);
//        booth12.setBoothCategories(Arrays.asList(boothCategory12));
//        boothCategoryRepository.save(boothCategory12);

// 부스 13
        Booth booth13 = new Booth(13L, "세무학과", "내용13", 0, "a-13", "이미지URL13",0, null, null, null, null);
//        BoothCategory boothCategory13 = new BoothCategory(13L, "카테고리13", booth13);
        boothRepository.save(booth13);
//        booth13.setBoothCategories(Arrays.asList(boothCategory13));
//        boothCategoryRepository.save(boothCategory13);

// 부스 14
        Booth booth14 = new Booth(14L, "중국학과", "내용14", 0, "a-14", "이미지URL14",0, null, null, null, null);
//        BoothCategory boothCategory14 = new BoothCategory(14L, "카테고리14", booth14);
        boothRepository.save(booth14);
//        booth14.setBoothCategories(Arrays.asList(boothCategory14));
//        boothCategoryRepository.save(boothCategory14);

// 부스 15
        Booth booth15 = new Booth(15L, "건축공학과", "내용15", 0, "a-15", "이미지URL15",0, null, null, null, null);
//        BoothCategory boothCategory15 = new BoothCategory(15L, "카테고리15", booth15);
        boothRepository.save(booth15);
//        booth15.setBoothCategories(Arrays.asList(boothCategory15));
//        boothCategoryRepository.save(boothCategory15);

        // 부스 16
        Booth booth16 = new Booth(16L, "임상병리학과", "내용16", 0, "a-16", "이미지URL16", 0,null, null, null, null);
//        BoothCategory boothCategory16 = new BoothCategory(16L, "카테고리16", booth16);
        boothRepository.save(booth16);
//        booth16.setBoothCategories(Arrays.asList(boothCategory16));
//        boothCategoryRepository.save(boothCategory16);

// 부스 17
        Booth booth17 = new Booth(17L, "보건행정학과", "내용17", 0, "a-17", "이미지URL17",0, null, null, null, null);
//        BoothCategory boothCategory17 = new BoothCategory(17L, "카테고리17", booth17);
        boothRepository.save(booth17);
//        booth17.setBoothCategories(Arrays.asList(boothCategory17));
//        boothCategoryRepository.save(boothCategory17);

// 부스 18
        Booth booth18 = new Booth(18L, "일어일문학과", "내용18", 0, "a-18", "이미지URL18", 0,null, null, null, null);
//        BoothCategory boothCategory18 = new BoothCategory(18L, "카테고리18", booth18);
        boothRepository.save(booth18);
//        booth18.setBoothCategories(Arrays.asList(boothCategory18));
//        boothCategoryRepository.save(boothCategory18);

// 부스 19
        Booth booth19 = new Booth(19L, "스마트팜학과", "내용19", 0, "a-19", "이미지URL19",0, null, null, null, null);
//        BoothCategory boothCategory19 = new BoothCategory(19L, "카테고리19", booth19);
        boothRepository.save(booth19);
//        booth19.setBoothCategories(Arrays.asList(boothCategory19));
//        boothCategoryRepository.save(boothCategory19);

// 부스 20
        Booth booth20 = new Booth(20L, "바이오헬스컨디셔닝학과", "내용20", 0, "a-20", "이미지URL20",0, null, null, null, null);
//        BoothCategory boothCategory20 = new BoothCategory(20L, "카테고리20", booth20);
        boothRepository.save(booth20);
//        booth20.setBoothCategories(Arrays.asList(boothCategory20));
//        boothCategoryRepository.save(boothCategory20);

        // 부스 21
        Booth booth21 = new Booth(21L, "건축학과", "내용21", 0, "a-21", "이미지URL21",0, null, null, null, null);
//        BoothCategory boothCategory21 = new BoothCategory(21L, "카테고리21", booth21);
        boothRepository.save(booth21);
//        booth21.setBoothCategories(Arrays.asList(boothCategory21));
//        boothCategoryRepository.save(boothCategory21);

// 부스 22
        Booth booth22 = new Booth(22L, "빅데이터경영공학과", "내용22", 0, "a-22", "이미지URL22",0, null, null, null, null);
//        BoothCategory boothCategory22 = new BoothCategory(22L, "카테고리22", booth22);
        boothRepository.save(booth22);
//        booth22.setBoothCategories(Arrays.asList(boothCategory22));
//        boothCategoryRepository.save(boothCategory22);

// 부스 23
        Booth booth23 = new Booth(23L, "영어과", "내용23", 0, "a-23", "이미지URL23", 0,null, null, null, null);
//        BoothCategory boothCategory23 = new BoothCategory(23L, "카테고리23", booth23);
        boothRepository.save(booth23);
//        booth23.setBoothCategories(Arrays.asList(boothCategory23));
//        boothCategoryRepository.save(boothCategory23);

// 부스 24
        Booth booth24 = new Booth(24L, "호텔경영학과", "내용24", 0, "a-24", "이미지URL24",0, null, null, null, null);
//        BoothCategory boothCategory24 = new BoothCategory(24L, "카테고리24", booth24);
        boothRepository.save(booth24);
//        booth24.setBoothCategories(Arrays.asList(boothCategory24));
//        boothCategoryRepository.save(boothCategory24);

// 부스 25
        Booth booth25 = new Booth(25L, "시각미디어디자인학과", "내용25", 0, "a-25", "이미지URL25", 0,null, null, null, null);
//        BoothCategory boothCategory25 = new BoothCategory(25L, "카테고리25", booth25);
        boothRepository.save(booth25);
//        booth25.setBoothCategories(Arrays.asList(boothCategory25));
//        boothCategoryRepository.save(boothCategory25);

        // 부스 26
        Booth booth26 = new Booth(26L, "경영학과", "내용26", 0, "a-26", "이미지URL26",0, null, null, null, null);
//        BoothCategory boothCategory26 = new BoothCategory(26L, "카테고리26", booth26);
        boothRepository.save(booth26);
//        booth26.setBoothCategories(Arrays.asList(boothCategory26));
//        boothCategoryRepository.save(boothCategory26);

// 부스 27
        Booth booth27 = new Booth(27L, "스포츠건강관리학과", "내용27", 0, "a-27", "이미지URL27",0, null, null, null, null);
//        BoothCategory boothCategory27 = new BoothCategory(27L, "카테고리27", booth27);
        boothRepository.save(booth27);
//        booth27.setBoothCategories(Arrays.asList(boothCategory27));
//        boothCategoryRepository.save(boothCategory27);

// 부스 28
        Booth booth28 = new Booth(28L, "관광경영학과", "내용28", 0, "a-28", "이미지URL28",0, null, null, null, null);
//        BoothCategory boothCategory28 = new BoothCategory(28L, "카테고리28", booth28);
        boothRepository.save(booth28);
//        booth28.setBoothCategories(Arrays.asList(boothCategory28));
//        boothCategoryRepository.save(boothCategory28);

// 부스 29
        Booth booth29 = new Booth(29L, "사회복지학과", "내용29", 0, "a-29", "이미지URL29",0, null, null, null, null);
//        BoothCategory boothCategory29 = new BoothCategory(29L, "카테고리29", booth29);
        boothRepository.save(booth29);
//        booth29.setBoothCategories(Arrays.asList(boothCategory29));
//        boothCategoryRepository.save(boothCategory29);

// 부스 30
        Booth booth30 = new Booth(30L, "광고홍보학과", "내용30", 0, "a-30", "이미지URL30",0, null, null, null, null);
//        BoothCategory boothCategory30 = new BoothCategory(30L, "카테고리30", booth30);
        boothRepository.save(booth30);
//        booth30.setBoothCategories(Arrays.asList(boothCategory30));
//        boothCategoryRepository.save(boothCategory30);


        // 부스 31
        Booth booth31 = new Booth(31L, "지능정보통신공학과", "내용31", 0, "a-31", "이미지URL31",0, null, null, null, null);
//        BoothCategory boothCategory31 = new BoothCategory(31L, "카테고리31", booth31);
        boothRepository.save(booth31);
//        booth31.setBoothCategories(Arrays.asList(boothCategory31));
//        boothCategoryRepository.save(boothCategory31);

// 부스 32
        Booth booth32 = new Booth(32L, "간호학과", "내용32", 0, "a-32", "이미지URL32", 0,null, null, null, null);
//        BoothCategory boothCategory32 = new BoothCategory(32L, "카테고리32", booth32);
        boothRepository.save(booth32);
//        booth32.setBoothCategories(Arrays.asList(boothCategory32));
//        boothCategoryRepository.save(boothCategory32);

// 부스 33
        Booth booth33 = new Booth(33L, "공간조형디자인학과", "내용33", 0, "a-33", "이미지URL33",0, null, null, null, null);
//        BoothCategory boothCategory33 = new BoothCategory(33L, "카테고리33", booth33);
        boothRepository.save(booth33);
//        booth33.setBoothCategories(Arrays.asList(boothCategory33));
//        boothCategoryRepository.save(boothCategory33);

// 부스 34
        Booth booth34 = new Booth(34L, "치위생학과", "내용34", 0, "a-34", "이미지URL34", 0,null, null, null, null);
//        BoothCategory boothCategory34 = new BoothCategory(34L, "카테고리34", booth34);
        boothRepository.save(booth34);
//        booth34.setBoothCategories(Arrays.asList(boothCategory34));
//        boothCategoryRepository.save(boothCategory34);

// 부스 35
        Booth booth35 = new Booth(35L, "부동산학과", "내용35", 0, "a-35", "이미지URL35",0, null, null, null, null);
//        BoothCategory boothCategory35 = new BoothCategory(35L, "카테고리35", booth35);
        boothRepository.save(booth35);
//        booth35.setBoothCategories(Arrays.asList(boothCategory35));
//        boothCategoryRepository.save(boothCategory35);

        // 부스 36
        Booth booth36 = new Booth(36L, "드론공간정보공학과", "내용36", 0, "a-36", "이미지URL36",0, null, null, null, null);
//        BoothCategory boothCategory36 = new BoothCategory(36L, "카테고리36", booth36);
        boothRepository.save(booth36);
//        booth36.setBoothCategories(Arrays.asList(boothCategory36));
//        boothCategoryRepository.save(boothCategory36);

// 부스 37
        Booth booth37 = new Booth(37L, "휴먼케어학과", "내용37", 0, "a-37", "이미지URL37", 0,null, null, null, null);
//        BoothCategory boothCategory37 = new BoothCategory(37L, "카테고리37", booth37);
        boothRepository.save(booth37);
//        booth37.setBoothCategories(Arrays.asList(boothCategory37));
//        boothCategoryRepository.save(boothCategory37);

// 부스 38
        Booth booth38 = new Booth(38L, "응급구조학과", "내용38", 0, "a-38", "이미지URL38", 0,null, null, null, null);
//        BoothCategory boothCategory38 = new BoothCategory(38L, "카테고리38", booth38);
        boothRepository.save(booth38);
//        booth38.setBoothCategories(Arrays.asList(boothCategory38));
//        boothCategoryRepository.save(boothCategory38);

        // 부스 39
        Booth booth39 = new Booth(39L, "굿네이버스", "내용39", 0, "a-38", "이미지URL39", 0,null, null, null, null);

//        BoothCategory boothCategory39 = new BoothCategory(39L, "먹거리", booth39);
        boothRepository.save(booth39);
//        booth39.setBoothCategories(Arrays.asList(boothCategory39));
//        boothCategoryRepository.save(boothCategory39);

        // 부스 40
        Booth booth40 = new Booth(40L, "라파엘", "내용40", 0, "a-40", "이미지URL40",0, null, null, null, null);
//        BoothCategory boothCategory40 = new BoothCategory(40L, "카테고리40", booth40);
        boothRepository.save(booth40);
//        booth40.setBoothCategories(Arrays.asList(boothCategory40));
//        boothCategoryRepository.save(boothCategory40);

// 부스 41
        Booth booth41 = new Booth(41L, "KOZ", "내용41", 0, "a-41", "이미지URL41",0, null, null, null, null);
//        BoothCategory boothCategory41 = new BoothCategory(41L, "카테고리41", booth41);
        boothRepository.save(booth41);
//        booth41.setBoothCategories(Arrays.asList(boothCategory41));
//        boothCategoryRepository.save(boothCategory41);

        // 부스 42
        Booth booth42 = new Booth(42L, "아랑", "내용42", 0, "a-42", "이미지URL42", 0,null, null, null, null);
//        BoothCategory boothCategory42 = new BoothCategory(42L, "카테고리42", booth42);
        boothRepository.save(booth42);
//        booth42.setBoothCategories(Arrays.asList(boothCategory42));
//        boothCategoryRepository.save(boothCategory42);

// 부스 43
        Booth booth43 = new Booth(43L, "창틀", "내용43", 0, "a-43", "이미지URL43", 0,null, null, null, null);
//        BoothCategory boothCategory43 = new BoothCategory(43L, "카테고리43", booth43);
        boothRepository.save(booth43);
//        booth43.setBoothCategories(Arrays.asList(boothCategory43));
//        boothCategoryRepository.save(boothCategory43);

// 부스 44
        Booth booth44 = new Booth(44L, "저스트댄스", "내용44", 0, "a-44", "이미지URL44", 0,null, null, null, null);
//        BoothCategory boothCategory44 = new BoothCategory(44L, "카테고리44", booth44);
        boothRepository.save(booth44);
//        booth44.setBoothCategories(Arrays.asList(boothCategory44));
//        boothCategoryRepository.save(boothCategory44);

// 부스 45
        Booth booth45 = new Booth(45L, "닉샷", "내용45", 0, "a-45", "이미지URL45", 0,null, null, null, null);
//        BoothCategory boothCategory45 = new BoothCategory(45L, "카테고리45", booth45);
        boothRepository.save(booth45);
//        booth45.setBoothCategories(Arrays.asList(boothCategory45));
//        boothCategoryRepository.save(boothCategory45);

// 부스 46
        Booth booth46 = new Booth(46L, "SENS", "내용46", 0, "a-46", "이미지URL46", 0,null, null, null, null);
//        BoothCategory boothCategory46 = new BoothCategory(46L, "카테고리46", booth46);
        boothRepository.save(booth46);
//        booth46.setBoothCategories(Arrays.asList(boothCategory46));
//        boothCategoryRepository.save(boothCategory46);

// 부스 47
        Booth booth47 = new Booth(47L, "C.C.C", "내용47", 0, "a-47", "이미지URL47", 0,null, null, null, null);
//        BoothCategory boothCategory47 = new BoothCategory(47L, "카테고리47", booth47);
        boothRepository.save(booth47);
//        booth47.setBoothCategories(Arrays.asList(boothCategory47));
//        boothCategoryRepository.save(boothCategory47);

        // 부스 48
        Booth booth48 = new Booth(48L, "로타랙트", "내용48", 0, "a-48", "이미지URL48",0, null, null, null, null);
//        BoothCategory boothCategory48 = new BoothCategory(48L, "카테고리48", booth48);
        boothRepository.save(booth48);
//        booth48.setBoothCategories(Arrays.asList(boothCategory48));
//        boothCategoryRepository.save(boothCategory48);

// 부스 49
        Booth booth49 = new Booth(49L, "소리터", "내용49", 0, "a-49", "이미지URL49", 0,null, null, null, null);
//        BoothCategory boothCategory49 = new BoothCategory(49L, "카테고리49", booth49);
        boothRepository.save(booth49);
//        booth49.setBoothCategories(Arrays.asList(boothCategory49));
//        boothCategoryRepository.save(boothCategory49);

// 부스 50
        Booth booth50 = new Booth(50L, "groove", "내용50", 0, "a-50", "이미지URL50", 0,null, null, null, null);
//        BoothCategory boothCategory50 = new BoothCategory(50L, "카테고리50", booth50);
        boothRepository.save(booth50);
//        booth50.setBoothCategories(Arrays.asList(boothCategory50));
//        boothCategoryRepository.save(boothCategory50);

// 부스 51
        Booth booth51 = new Booth(51L, "케렌시아", "내용51", 0, "a-51", "이미지URL51", 0,null, null, null, null);
//        BoothCategory boothCategory51 = new BoothCategory(51L, "카테고리51", booth51);
        boothRepository.save(booth51);
//        booth51.setBoothCategories(Arrays.asList(boothCategory51));
//        boothCategoryRepository.save(boothCategory51);

// 부스 52
        Booth booth52 = new Booth(52L, "새마을", "내용52", 0, "a-52", "이미지URL52", 0,null, null, null, null);
//        BoothCategory boothCategory52 = new BoothCategory(52L, "카테고리52", booth52);
        boothRepository.save(booth52);
//        booth52.setBoothCategories(Arrays.asList(boothCategory52));
//        boothCategoryRepository.save(boothCategory52);

// 부스 53
        Booth booth53 = new Booth(53L, "아름다운 사람들", "내용53", 0, "a-53", "이미지URL53", 0,null, null, null, null);
//        BoothCategory boothCategory53 = new BoothCategory(53L, "카테고리53", booth53);
        boothRepository.save(booth53);
//        booth53.setBoothCategories(Arrays.asList(boothCategory53));
//        boothCategoryRepository.save(boothCategory53);

        // 부스 54
        Booth booth54 = new Booth(54L, "제너시스", "내용54", 0, "a-54", "이미지URL54", 0,null, null, null, null);
//        BoothCategory boothCategory54 = new BoothCategory(54L, "카테고리54", booth54);
        boothRepository.save(booth54);
//        booth54.setBoothCategories(Arrays.asList(boothCategory54));
//        boothCategoryRepository.save(boothCategory54);

// 부스 55
        Booth booth55 = new Booth(55L, "SHOUT", "내용55", 0, "a-55", "이미지URL55", 0,null, null, null, null);
//        BoothCategory boothCategory55 = new BoothCategory(55L, "카테고리55", booth55);
        boothRepository.save(booth55);
//        booth55.setBoothCategories(Arrays.asList(boothCategory55));
//        boothCategoryRepository.save(boothCategory55);

// 부스 56
        Booth booth56 = new Booth(56L, "NVP", "내용56", 0, "a-56", "이미지URL56", 0,null, null, null, null);
//        BoothCategory boothCategory56 = new BoothCategory(56L, "카테고리56", booth56);
        boothRepository.save(booth56);
//        booth56.setBoothCategories(Arrays.asList(boothCategory56));
//        boothCategoryRepository.save(boothCategory56);

        // 부스 57
        Booth booth57 = new Booth(57L, "노스텔지어", "내용57", 0, "a-57", "이미지URL57",0, null, null, null, null);
//        BoothCategory boothCategory57 = new BoothCategory(57L, "카테고리57", booth57);
        boothRepository.save(booth57);
//        booth57.setBoothCategories(Arrays.asList(boothCategory57));
//        boothCategoryRepository.save(boothCategory57);

// 부스 58
        Booth booth58 = new Booth(58L, "아우트런스", "내용58", 0, "a-58", "이미지URL58",0, null, null, null, null);
//        BoothCategory boothCategory58 = new BoothCategory(58L, "카테고리58", booth58);
        boothRepository.save(booth58);
//        booth58.setBoothCategories(Arrays.asList(boothCategory58));
//        boothCategoryRepository.save(boothCategory58);

// 부스 59
        Booth booth59 = new Booth(59L, "애니지크", "내용59", 0, "a-59", "이미지URL59",0, null, null, null, null);
//        BoothCategory boothCategory59 = new BoothCategory(59L, "카테고리59", booth59);
        boothRepository.save(booth59);
//        booth59.setBoothCategories(Arrays.asList(boothCategory59));
//        boothCategoryRepository.save(boothCategory59);

        // 부스 60
        Booth booth60 = new Booth(60L, "대학혁신지원사업단", "내용60", 0, "a-60", "이미지URL60",0, null, null, null, null);
//        BoothCategory boothCategory60 = new BoothCategory(60L, "카테고리60", booth60);
        boothRepository.save(booth60);
//        booth60.setBoothCategories(Arrays.asList(boothCategory60));
//        boothCategoryRepository.save(boothCategory60);

// 부스 61
        Booth booth61 = new Booth(61L, "LEAF", "내용61", 0, "a-61", "이미지URL61",0, null, null, null, null);
//        BoothCategory boothCategory61 = new BoothCategory(61L, "카테고리61", booth61);
        boothRepository.save(booth61);
//        booth61.setBoothCategories(Arrays.asList(boothCategory61));
//        boothCategoryRepository.save(boothCategory61);

// 부스 62
        Booth booth62 = new Booth(62L, "카르페디엠", "내용62", 0, "a-62", "이미지URL62",0, null, null, null, null);
//        BoothCategory boothCategory62 = new BoothCategory(62L, "카테고리62", booth62);
        boothRepository.save(booth62);
//        booth62.setBoothCategories(Arrays.asList(boothCategory62));
//        boothCategoryRepository.save(boothCategory62);

        // 부스 63
        Booth booth63 = new Booth(63L, "C.O.M.E", "내용63", 0, "a-63", "이미지URL63", 0,null, null, null, null);
//        BoothCategory boothCategory63 = new BoothCategory(63L, "카테고리63", booth63);
        boothRepository.save(booth63);
//        booth63.setBoothCategories(Arrays.asList(boothCategory63));
//        boothCategoryRepository.save(boothCategory63);

// 부스 64
        Booth booth64 = new Booth(64L, "NSTV", "내용64", 0, "a-64", "이미지URL64", 0,null, null, null, null);
//        BoothCategory boothCategory64 = new BoothCategory(64L, "카테고리64", booth64);
        boothRepository.save(booth64);
//        booth64.setBoothCategories(Arrays.asList(boothCategory64));
//        boothCategoryRepository.save(boothCategory64);

// 부스 65
        Booth booth65 = new Booth(65L, "애드밴처", "내용65", 0, "a-65", "이미지URL65", 0,null, null, null, null);
//        BoothCategory boothCategory65 = new BoothCategory(65L, "카테고리65", booth65);
        boothRepository.save(booth65);
//        booth65.setBoothCategories(Arrays.asList(boothCategory65));
//        boothCategoryRepository.save(boothCategory65);

// 부스 66
        Booth booth66 = new Booth(66L, "아메리타트", "내용66", 0, "a-66", "이미지URL66", 0,null, null, null, null);
//        BoothCategory boothCategory66 = new BoothCategory(66L, "카테고리66", booth66);
        boothRepository.save(booth66);
//        booth66.setBoothCategories(Arrays.asList(boothCategory66));
//        boothCategoryRepository.save(boothCategory66);

// 부스 67
        Booth booth67 = new Booth(67L, "SCON", "내용67", 0, "a-67", "이미지URL67", 0,null, null, null, null);
//        BoothCategory boothCategory67 = new BoothCategory(67L, "카테고리67", booth67);
        boothRepository.save(booth67);
//        booth67.setBoothCategories(Arrays.asList(boothCategory67));
//        boothCategoryRepository.save(boothCategory67);

// 부스 68
        Booth booth68 = new Booth(68L, "대외국제교류처1", "내용68", 0, "a-68", "이미지URL68", 0,null, null, null, null);
//        BoothCategory boothCategory68 = new BoothCategory(68L, "카테고리68", booth68);
        boothRepository.save(booth68);
//        booth68.setBoothCategories(Arrays.asList(boothCategory68));
//        boothCategoryRepository.save(boothCategory68);

        // 부스 69
        Booth booth69 = new Booth(69L, "대외국제교류처2", "내용69", 0, "a-69", "이미지URL69", 0,null, null, null, null);
//        BoothCategory boothCategory69 = new BoothCategory(69L, "카테고리69", booth69);
        boothRepository.save(booth69);
//        booth69.setBoothCategories(Arrays.asList(boothCategory69));
//        boothCategoryRepository.save(boothCategory69);

// 부스 70
        Booth booth70 = new Booth(70L, "대외국제교류처3", "내용70", 0, "a-70", "이미지URL70", 0,null, null, null, null);
//        BoothCategory boothCategory70 = new BoothCategory(70L, "카테고리70", booth70);
        boothRepository.save(booth70);
//        booth70.setBoothCategories(Arrays.asList(boothCategory70));
//        boothCategoryRepository.save(boothCategory70);

// 푸드트럭 1

        Booth foodTruck1 = new Booth(71L,"고깃집-스테이크",null,0,null,getBoothImgList("foodTruckImage").get(11),0,null,null,null,null);
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
        Booth foodTruck2 = new Booth(72L,"곱창야시장",null,0,null,getBoothImgList("foodTruckImage").get(1),0,null,null,null,null);
        BoothCategory boothCategory2 = new BoothCategory(2L,"먹거리",foodTruck2);
        boothRepository.save(foodTruck2);
        boothCategoryRepository.save(boothCategory2);
        List<Menu> menus1 = new ArrayList<>();
        menus1.add(new Menu(4L,"곱창순대볶음(중)","15,000",foodTruck2));
        menus1.add(new Menu(5L,"곱창순대볶음(대)","22,000",foodTruck2));
        menuRepository.saveAll(menus1);
        foodTruck2.setMenus(menus1);

// 푸드트럭3
        Booth foodTruck3 = new Booth(73L,"다온푸드",null,0,null,getBoothImgList("foodTruckImage").get(3),0,null,null,null,null);
        BoothCategory boothCategory3 = new BoothCategory(3L,"먹거리",foodTruck3);
        boothRepository.save(foodTruck3);
        boothCategoryRepository.save(boothCategory3);
        List<Menu> menus2 = new ArrayList<>();
        menus2.add(new Menu(6L,"크림,칠리새우(중)","10,000",foodTruck3));
        menus2.add(new Menu(7L,"크림,칠리새우(대)","15,000",foodTruck3));
        menuRepository.saveAll(menus2);
        foodTruck3.setMenus(menus2);

        // 푸드트럭4
        Booth foodTruck4 = new Booth(74L,"대감님댁",null,0,null,getBoothImgList("foodTruckImage").get(0),0,null,null,null,null);
        BoothCategory boothCategory4 = new BoothCategory(4L,"먹거리",foodTruck4);
        boothRepository.save(foodTruck4);
        boothCategoryRepository.save(boothCategory4);
        List<Menu> menus3 = new ArrayList<>();
        menus3.add(new Menu(8L,"닭강정(중)","10,000",foodTruck4));
        menus3.add(new Menu(9L,"닭강정(대)","15,000",foodTruck4));
        menuRepository.saveAll(menus3);
        foodTruck4.setMenus(menus3);

        // 푸드트럭5
        Booth foodTruck5 = new Booth(75L,"마이요거트립",null,0,null,getBoothImgList("foodTruckImage").get(4),0,null,null,null,null);
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
        Booth foodTruck6 = new Booth(76L,"바바파파",null,0,null,getBoothImgList("foodTruckImage").get(12),0,null,null,null,null);
        BoothCategory boothCategory6 = new BoothCategory(6L,"먹거리",foodTruck6);
        boothRepository.save(foodTruck6);
        boothCategoryRepository.save(boothCategory6);
        List<Menu> menus5 = new ArrayList<>();
        menus5.add(new Menu(13L,"타코야끼10알","6,000",foodTruck6));
        menuRepository.saveAll(menus5);
        foodTruck6.setMenus(menus5);

        // 푸드트럭7
        Booth foodTruck7 = new Booth(77L,"부엉이푸드",null,0,null,getBoothImgList("foodTruckImage").get(8),0,null,null,null,null);
        BoothCategory boothCategory7 = new BoothCategory(7L,"먹거리",foodTruck7);
        boothRepository.save(foodTruck7);
        boothCategoryRepository.save(boothCategory7);
        List<Menu> menus6 = new ArrayList<>();
        menus6.add(new Menu(14L,"닭꼬치","5,000",foodTruck7));
        menuRepository.saveAll(menus6);
        foodTruck7.setMenus(menus6);

        // 푸드트럭8
        Booth foodTruck8 = new Booth(78L,"썬플라워",null,0,null,getBoothImgList("foodTruckImage").get(14),0,null,null,null,null);
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
        Booth foodTruck9 = new Booth(79L,"어더아사",null,0,null,getBoothImgList("foodTruckImage").get(2),0,null,null,null,null);
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
        Booth foodTruck10 = new Booth(80L,"엉클",null,0,null,getBoothImgList("foodTruckImage").get(7),0,null,null,null,null);
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
        Booth foodTruck11 = new Booth(81L,"에페스케밥",null,0,null,getBoothImgList("foodTruckImage").get(10),0,null,null,null,null);
        BoothCategory boothCategory11 = new BoothCategory(11L,"먹거리",foodTruck11);
        boothRepository.save(foodTruck11);
        boothCategoryRepository.save(boothCategory11);
        List<Menu> menus10 = new ArrayList<>();
        menus10.add(new Menu(26L,"케밥","7,000 ~ 9,000",foodTruck11));
        menus10.add(new Menu(27L,"터키아이스크림","4,000",foodTruck11));
        menuRepository.saveAll(menus10);
        foodTruck11.setMenus(menus10);

        // 푸드트럭12
        Booth foodTruck12 = new Booth(82L,"오션푸드",null,0,null,getBoothImgList("foodTruckImage").get(5),0,null,null,null,null);
        BoothCategory boothCategory12 = new BoothCategory(12L,"먹거리",foodTruck12);
        boothRepository.save(foodTruck12);
        boothCategoryRepository.save(boothCategory12);
        List<Menu> menus11 = new ArrayList<>();
        menus11.add(new Menu(28L,"슬러시","3,500",foodTruck12));
        menus11.add(new Menu(29L,"캐릭터슬러시","6,000",foodTruck12));
        menuRepository.saveAll(menus11);
        foodTruck12.setMenus(menus11);

        // 푸드트럭13
        Booth foodTruck13 = new Booth(83L,"오션푸드2",null,0,null,getBoothImgList("foodTruckImage").get(6),0,null,null,null,null);
        BoothCategory boothCategory13 = new BoothCategory(13L,"먹거리",foodTruck13);
        boothRepository.save(foodTruck13);
        boothCategoryRepository.save(boothCategory13);
        List<Menu> menus12 = new ArrayList<>();
        menus12.add(new Menu(30L,"야끼소바","10,000",foodTruck13));
        menus12.add(new Menu(31L,"오코노미야끼","10,000",foodTruck13));
        menuRepository.saveAll(menus12);
        foodTruck13.setMenus(menus12);

        // 푸드트럭14
        Booth foodTruck14 = new Booth(84L,"요기분식",null,0,null,getBoothImgList("foodTruckImage").get(9),0,null,null,null,null);
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
        Booth foodTruck15 = new Booth(85L,"원탑푸드트럭",null,0,null,getBoothImgList("foodTruckImage").get(13),0,null,null,null,null);
        BoothCategory boothCategory15 = new BoothCategory(15L,"먹거리",foodTruck15);
        boothRepository.save(foodTruck15);
        boothCategoryRepository.save(boothCategory15);
        List<Menu> menus14 = new ArrayList<>();
        menus14.add(new Menu(35L,"불초밥 10p","11,000",foodTruck15));
        menus14.add(new Menu(36L,"연어초밥 10p","12,000",foodTruck15));
        menuRepository.saveAll(menus14);
        foodTruck15.setMenus(menus14);



    }


}
