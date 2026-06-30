package com.example.tem_on.product.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {

    FASHION("패션"),
    SHOES("신발"),
    BAG("가방"),
    ACCESSORY("액세서리"),

    ELECTRONIC("전자기기"),
    DIGITAL_DEVICE("디지털기기"),
    HOME_APPLIANCE("생활가전"),

    BEAUTY("뷰티"),
    FOOD("식품"),
    LIVING("생활용품"),
    SPORTS("스포츠"),
    TOY("완구"),
    BOOK("도서"),
    PET("반려동물"),
    BABY("유아동"),
    HEALTH("헬스/건강"),
    INTERIOR("인테리어"),
    LIFESTYLE("라이프스타일"),
    ETC("기타");

    private final String description;
}