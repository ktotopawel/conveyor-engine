import io.javalin.Javalin;

void main() {
    var app = Javalin.create(cfg -> {
        cfg.routes.get("/", ctx -> ctx.result("Hello World!"));
    }).start(7070);
}