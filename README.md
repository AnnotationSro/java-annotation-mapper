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

JAM library contains an Annotation Processor that generates effective Java code 
to handle the mapping from one object (or objects) to other at compile time. 
Result of this process is:
  * efficiently generated Java class 
  * very fast code in runtime 
  * without other additional dependencies at runtime (only jam-common.jar ~ 9.5 KB) 

## Why JAM mapper is good to use
JAM library:
   * significantly reduces developer's time
   * improvement quality of code
   * improvement speed of development
     * a lot of problems are resolved by mapper generator
   * mapper is generated during compilation
     * detected problem at compilation time
     * runtime is very fast 
   * solving a lot of often problems as:
     * problem with cyclic dependencies in runtime
     * problem with object instances
     * problem with shared context data (as cache of instances)
     * problem with different but compatible types between internal and external sources (WebServices,JAXB,...)
     * support for injection containers as CDI or Spring
    

## How to add it into project ?

First add library *jam-processor* as a *provided* dependency and *jam-common* as a *compile* dependency to your build.
```xml
<dependencies>
     <!-- VARIANT for JDK v 11+ -->
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
     
     
     <!-- VARIANT for old JDK (version 8-10) -->
     <dependency>
          <groupId>sk.annotation.library.jam</groupId>
          <artifactId>jam-common</artifactId>
          <version>${jam.version}</version>
          <classifier>jdk8</classifier>
     </dependency>
     <dependency>
          <groupId>sk.annotation.library.jam</groupId>
          <artifactId>jam-processor</artifactId>
          <version>${jam.version}</version>
          <classifier>jdk8</classifier>
          <scope>provided</scope>
     </dependency>
</dependencies>
```

## How to create a mapper?
Define *Mapper* interface (or abstract class) describing the mapping you want. Follow example is with Spring support:

```java
@Mapper
@EnableSpring   // this is optional, generated interface is annotated for spring with @Component 
public interface SimpleMapper {
    
    // Imutable mapping
    OutBean map(InBean in);
    
    // Update graph
    OutBean update(InBean in, @Return OutBean out);
}
```

## How to use it?

Getting mapper instance directly:
```java
public class UsingMapper {

    public void main(String[] args) {
        SimpleMapper mapper = MapperUtil.getMapper(SimpleMapper.class);

        // example with immutable mapping
        OutBean res = mapper.map(in);
    }
}
```

Using mapper in Spring bean:
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

**Warning**: WEB, documentation, examples and new features are in progress.

Please, follow examples in jam-tests for learn features. 
