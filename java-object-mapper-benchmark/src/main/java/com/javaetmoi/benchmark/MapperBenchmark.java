package com.javaetmoi.benchmark;

import com.javaetmoi.benchmark.mapping.mapper.OrderMapper;
import com.javaetmoi.benchmark.mapping.mapper.dozer.DozerMapper;
import com.javaetmoi.benchmark.mapping.mapper.fmap.FMapMapper;
import com.javaetmoi.benchmark.mapping.mapper.jmapper.JMapperMapper;
import com.javaetmoi.benchmark.mapping.mapper.manual.ManualMapper;
import com.javaetmoi.benchmark.mapping.mapper.mapstruct.MapStructMapper;
import com.javaetmoi.benchmark.mapping.mapper.modelmapper.ModelMapper;
import com.javaetmoi.benchmark.mapping.mapper.orika.OrikaMapper;
import com.javaetmoi.benchmark.mapping.mapper.selma.SelmaMapper;
import com.javaetmoi.benchmark.mapping.model.dto.OrderDTO;
import com.javaetmoi.benchmark.mapping.model.entity.Order;
import com.javaetmoi.benchmark.mapping.model.entity.OrderFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;

@State(Scope.Benchmark)

@Fork(value = 3, warmups = 3)
@Warmup(iterations = 5)
@Measurement(iterations = 5)

//@BenchmarkMode(Mode.All)
public class MapperBenchmark {
    static final int iterations = 5;

    @Param({"FMapMapper",  "Manual", "MapStruct",  "Selma", "JMapper", "Orika", "ModelMapper", "Dozer"})
    private String type;

    private OrderMapper mapper;
    private Order order;

    @Setup(Level.Trial)
    public void setup(){
        switch (type) {
            case "Dozer":
                mapper = new DozerMapper();
                break;
            case "FMapMapper":
                mapper = new FMapMapper();
                break;
            case "Orika":
                mapper = new OrikaMapper();
                break;
            case "ModelMapper":
                mapper = new ModelMapper();
                break;
            case "MapStruct":
                mapper = new MapStructMapper();
                break;
            case "Selma":
                mapper = new SelmaMapper();
                break;
            case "JMapper":
                mapper = new JMapperMapper();
                break;
            case "Manual":
                mapper = new ManualMapper();
                break;
            default:
                throw new IllegalStateException("Unknown type: " + type);
        }
    }

    @Setup(Level.Iteration)
    public void preInit(){
        order = OrderFactory.buildOrder();
    }

    @Benchmark
    public OrderDTO mapper() {
        return mapper.map(order);
    }

    public static void main(String... args) throws Exception {
        Options opts = new OptionsBuilder()
                .include(MapperBenchmark.class.getSimpleName())
//                .warmupIterations(5)
//                .measurementIterations(5)
                .warmupIterations(1)
                .measurementIterations(1)
				.jvmArgs("-server")
				.forks(2)
				.warmupForks(1)
				.resultFormat(ResultFormatType.TEXT)
                .build();

        Collection<RunResult> results = new Runner(opts).run();
        for (RunResult result : results) {
            Result<?> r = result.getPrimaryResult();
            System.out.println("API replied benchmark score: "
                    + r.getScore() + " "
                    + r.getScoreUnit() + " over "
                    + r.getStatistics().getN() + " iterations");
        }
    }

}
