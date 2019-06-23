package sk.annotation.library.mapper.fast.utils;

public class MapperRunCtxDataHolder {
    private MapperRunCtxDataHolder() {
        throw new IllegalStateException("This method cannot be implemented!");
    }

    static public final ThreadLocal<MapperRunCtxData> data = new ThreadLocal<>();
    static public MapperRunCtxData createDefaultContext() {
        MapperRunCtxData ctx = new MapperRunCtxData();
        data.set(ctx);
        return ctx;
    }
}
