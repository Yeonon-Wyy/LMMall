package top.yeonon.repository;

import org.apache.ibatis.annotations.Mapper;

import top.yeonon.entity.Category;


import java.util.List;

@Mapper
public interface CategoryRepository {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectParallelChildrenCategoryByParentId(Integer parentId);
}