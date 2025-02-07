package com.ttabong.entity.recruit;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 new User() 막기
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Builder에서만 생성 가능
@Builder
@Table(name = "Category")
public class Category {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Category_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "parent")
    private Set<Category> categories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "category")
    private Set<Template> templates = new LinkedHashSet<>();

}