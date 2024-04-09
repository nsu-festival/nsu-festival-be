//package com.example.nsu_festival.booth;
//
//import com.example.nsu_festival.domain.booth.entity.Booth;
//import com.example.nsu_festival.domain.booth.entity.BoothCategory;
//import com.example.nsu_festival.domain.booth.repository.BoothCategoryRepository;
//import com.example.nsu_festival.domain.booth.repository.BoothRepository;
//import org.junit.jupiter.api.Test;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@SpringBootTest
//public class BoothServiceImplTest {
//
//    @Autowired
//    BoothRepository boothRepository;
//
//    @Autowired
//    BoothCategoryRepository boothCategoryRepository;
//
//    @Autowired
//    BoothImageRepository boothImageRepository;
//    @Autowired
//    ModelMapper modelMapper;
//
//    @Test
//    @Transactional
//    void insertBooth(){
//        Booth insertBooth =boothRepository.save(Booth.builder()
//
//                .boothId(1L)
//                .countLike(1L)
//                .area("1")
//                .content("1")
//                .title("1")
//                .build()
//        );
//
//        BoothCategory insertBoothCategory = boothCategoryRepository.save(
//                BoothCategory.builder()
//                        .boothCategoryId(1L)
//                        .category("game")
//                        .booth(insertBooth)
//                        .build()
//
//        );
//
//        BoothImage insertBoothImage = boothImageRepository.save(
//                BoothImage.builder()
//                        .boothImageId(1L)
//                        .booth(insertBooth)
//                        .build()
//        );
//        List<BoothDto> findAllBooths = boothRepository.findAll().stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//
//
//
//    }
//    BoothDto convertToDto(Booth booth){
//        return modelMapper.map(booth,BoothDto.class);
//    }
//}
