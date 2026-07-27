package vix.local.api.shared.tenant;

public class TenantContext {
    private static final ThreadLocal<String> currentSchema = new ThreadLocal<>();

    public static void setSchema(String schema) {
        currentSchema.set(schema);
    }

    public static String getSchema() {
        return currentSchema.get();
    }

    public static void clear() {
        currentSchema.remove();
    }
}
