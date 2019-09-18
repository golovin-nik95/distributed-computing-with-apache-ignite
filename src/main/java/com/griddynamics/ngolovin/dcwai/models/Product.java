package com.griddynamics.ngolovin.dcwai.models;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.io.Serializable;

@Data
public class Product implements Serializable {

    @CsvBindByName(column = "uniq_id")
    private String uniqId;

    @CsvBindByName(column = "sku")
    private String sku;

    @CsvBindByName(column = "name_title")
    private String nameTitle;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "list_price")
    private String listPrice;

    @CsvBindByName(column = "sale_price")
    private String salePrice;

    @CsvBindByName(column = "category")
    private String category;

    @CsvBindByName(column = "category_tree")
    private String categoryTree;

    @CsvBindByName(column = "average_product_rating")
    private String averageRating;

    @CsvBindByName(column = "product_url")
    private String url;

    @CsvBindByName(column = "product_image_urls")
    private String imageUrls;

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "total_number_reviews")
    private String totalNumberReviews;

    @CsvBindByName(column = "Reviews")
    private String reviews;
}