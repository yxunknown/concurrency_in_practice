# 同步容器类
同步容器类包括Vector和Hashtable，同步容器类都是线程安全的，
但是在某些情况下仍然需要额外的客户端同步锁。在同步容器类上进行
复合操作，如迭代访问元素、或指定访问某个元素以及若没有则添加的
操作，在没有客户端加锁的情况下仍然是安全的，但是如果其他线程
并发的修改容器，这可能会出现一些以外的状况。

容器类的迭代器，在进行迭代的时候没有考虑并发修改的问题。因此
容器在迭代的时候，如果被并发线程修改，就会抛出从ConcurrentModificationException。

如果想要避免ConcurrentModificationException异常，则在进行
迭代的时候就需要持有容器类的锁。

如果容器类有大量的元素的时候，进行迭代访问可能会耗费大量的时间。
而进行迭代的线程长时间占有锁，导致其他现场处于等待状态，
降低了cpu的利用率。

如果想要避免迭代线程长时间占有容器锁的问题，可以使用容器的克隆
对象，在副本上进行迭代。

容器的hashCode或toString方法也会间接的进行迭代操作；同样，
containsAll、removeAll、retainAll等方法，以及把容器作为
构造参数的构造函数，都会对容器进行迭代，这些间接的迭代操作都
可能抛出ConcurrentModificationException。

# 并发容器类
并发容器时针对多线程并发访问设计的，解决了同步容器内部对元素
串行化访问导致的性能问题。

ConcurrentHashMap用来替代基于散列的Map。  
CopyOnWriteArrayList用于遍历操作为主要操作的List。  
通过并发容器来代替同步容器类，可以极大地提高伸缩性并降低风险。

Java 5.0 后提供了两种新的容器类型：Queue和BlockingQueue。
Queue用来临时保存一组待处理的数据。对Queue的操作不会被阻塞，
如果队列为空，则返回空值。BlockingQueue对Queue进行了扩展，
加入了可阻塞的插入和获取等操作。阻塞队列很适合用在生产者-消费者
模式中。

## ConcurrentHashMap
同步容器在每个操作期间都持有一个锁，如果对于一些耗时的操作，
则会使得其他线程无法访问该容器，从而降低了同步容器的的访问性能。

ConcurrentHashMap也是基于散列的Map，但是对比普通的HashMap，
他采用了完全不同的加锁策略来提供更高的并发访问和伸缩性。ConcurrentHashMap
使用粒度细的分段锁机制，实现更大程度的共享。它允许任意数量的读取
线程并发的访问Map，也运行一定数量的写入线程并发的修改Map。
ConcurrentHashMap提高了多线程环境的吞吐量。

ConcurrentHashMap在迭代过程中不会抛出ConcurrentModificationException
异常，因此不需要在迭代时对容器加锁。因为ConcurrentHashMap返回的迭代器
具有弱一致性，而并非及时失败。这种弱一致性可以容忍并发的修改，但不保证
可以将修改后的数据反应给容器。

由于这种弱一致性，虽然带了来性能的优势，但也在其他地方带来一些损失。
比如size和isEmpty，get，put，removeKey，containsKey等操作
都被弱化了，size返回的是一个估计值，而非容器元素数量的精确值。

大多数的情况下，ConcurrentHashMap都有着更多的优势及更少的劣势，
用ConcurrentHashMap来代替同步Map能进一步提高代码的可伸缩性，
只有当程序需要加锁Map进行独占访问时，才应该放弃ConcurrentHashMap。

## ConcurrentMap
ConcurrentMap提供了额外的原子锁，来实现如 若没有则添加、若相等则移出、
若相等则替换等。如果需要在现有的Map中添加诸如之类的功能，则可以考虑
使用ConcurrentMap。
```java
public interface ConcurrentMap<K,V> extends Map<K, V> {
    /**
    * 仅当key没有映射值时才插入
    */
    V putIfAbsent(K key, V value);
    
    /**
    * 仅当key没有映射到value时才删除
    */
    boolean remove(K key, V value);
    
    /**
    * 仅当key映射到oldValue时才替换为newValue
    */
    boolean replace(K key, V oldValue, V newValue);
    
    /**
    * 仅当K被映射到某个值才替换为newValue
    */
    V replace(K key, V newValue);
}
```

## CopyOnWriteArrayList
CopyOnWriteArrayList用于代替同步的List， CopyOnWriteArraySet
用于代理同步的Set。这些类在迭代期间不需要对容易进行加锁或复制。 

CopyOnWrite（写入时复制）容器的安全性在于只要正确发布一个事实不可变
对象，那么在访问该对象的时候就不需要进一步的同步；每次修改，都会创建
新的容器副本，从而实现可变性。

对于这类容器，修改时由于需要复制底层的数组，往往开销比较大，因此
如果不是迭代操作远远多于修改操作时，不应该使用CopyOnWrite类型的
容器。

## BlockingQueue
阻塞队列提供了可阻塞的put和take方法，以及支持定时的offer和pull方法。
如果队列满了，那么put方法将阻塞到有空间可用为止。

**在构建高可靠的应用程序时，有界队列是一个强大的资源管理工具：他们能抑制并防止
产生过多的工作项，使应用程序在负载过多的情况下更健壮。**



