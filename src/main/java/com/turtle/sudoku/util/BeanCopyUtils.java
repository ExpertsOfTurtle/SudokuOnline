package com.turtle.sudoku.util;

import java.util.List;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class BeanCopyUtils {

    private static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    private static MapperFacade mapper = mapperFactory.getMapperFacade();
    public static <F, T> T map(F from, Class<T> toClazz) {
        return mapper.map(from, toClazz);
    }
    
    public static <F, T> List<T> mapList(Iterable<F> froms, Class<T> toClazz) {
        return mapper.mapAsList(froms, toClazz);
    }
    
}
