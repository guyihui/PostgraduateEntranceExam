package com.sjtu.hashtable;

public interface MyHashtable {
    String getImplementation();

    // 将键值对(key,value)插⼊入到哈希表中，若key已存在，则替换当前的value
    Boolean set(Integer key, Integer value);

    // 给出指定key做对应的value，若key不在表中，则返回null。
    Integer get(Integer key);

    // 将指定key和其对应的value从哈希表中删除，若key不在表中，则不做任何操作。
    Boolean delete(Integer key);

    // 为了保证哈希表的性能，当哈希表中的元素个数超过哈希表位置数的一半时， 需要触发一次哈希表扩容
    Integer resize();

    Integer size();

    Integer getCapacity();
}
