<!--                                                                           -->
<!--  Copyright 2019 - Annotation,s.r.o.                                         -->
<!--                                                                           -->
<!--  Licensed under the Apache License, Version 2.0 (the "License");          -->
<!--  you may not use this file except in compliance with the License.         -->
<!--  You may obtain a copy of the License at                                  -->
<!--                                                                           -->
<!--           http://www.apache.org/licenses/LICENSE-2.0                      -->
<!--                                                                           -->
<!--  Unless required by applicable law or agreed to in writing, software      -->
<!--  distributed under the License is distributed on an "AS IS" BASIS,        -->
<!--  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. -->
<!--  See the License for the specific language governing permissions and      -->
<!--  limitations under the License.                                           -->
<!--                                                                           -->

## JAM as Java Annotation Mapper is mapping at compile time without reflection !

# What is JAM ?

JAM stands for ***Java Annotation Mapper*** is inspired by 
project **SELMA** (visit: http://www.selma-java.org/).

JAM library contains an Annotation Processor that generate effective Java code 
to handle the mapping from one object (or objects) to other at compile time. 
Result of this process is:
  * effective generated java class 
  * code is very fast in runtime 
  * without other additional dependencies at runtime (only jam-common.jar ~ 9.5 KB) 

JAM library significantly reduces developer's time.


## How does it add to project ?

First add library jam-processor as a provided dependency and jam-common as a compile dependency to your build.
```xml
<dependency>
    <groupId>sk.annotation.library.jam</groupId>
    <artifactId>jam-common</artifactId>
    <version>${jam.version}</version>
</dependency>

<dependency>
    <groupId>sk.annotation.library.jam</groupId>
    <artifactId>jam-processor</artifactId>
    <scope>provided</scope>
    <version>${jam.version}</version>
</dependency>
```

## How does it create mapper?
Define Mapper interface (or abstract class) describing the mapping you want. Follow example is with Spring support:

```java
@JamMapper
@EnableSpring
public interface SimpleMapper {
    
    // Imutable mapping
    OutBean map(InBean in);
    
    // Update graph
    OutBean update(InBean in, @Return OutBean out);
}
```

## How does it use?

Getting mapper instance directly:
```java
    SimpleMapper mapper = MapperInstanceUtil.getMapper(SimpleMapper.class);
```

Using mapper in spring bean:
```java
@Component
public class AnySpringService {
    @Autowired
    private SimpleMapper mapper;

    public void example() {
        // example with immutable mapping
        OutBean res = mapper.map(in);
    
        // example with updating
        OutBean dest = dao.getById(42);
        OutBean res = mapper.update(in, dest);
        // res is the updated bean dest with in values
    }
}
```

**Warning**: WEB, documentations, examples and new features are in progress.

