import controller.DatabaseController;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import repository.DatabaseRepository;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.vue.VueComponent;

public class Program {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start();
        app.config.enableWebjars();

        // View
        app.get("/", ctx -> ctx.redirect("/index"));
        app.get("/index", new VueComponent("index"));
        app.get("/bruker", new VueComponent("bruker"));
        app.get("/administrator", new VueComponent("administrator"));

        // controller
        DatabaseRepository repo = new DatabaseRepository();
        DatabaseController controller = new DatabaseController(repo);

        // API
        app.get("api/", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.jsonListIndex(ctx);
            }
        });
        app.get("api/bruker", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.jsonBruker(ctx);
            }
        });
        app.get("/api/administrator", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.jsonListIndex(ctx);
            }
        });
        app.post("api/lag", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.lagParkeringsplass(ctx);
                ctx.redirect("/index");
            }
        });
        app.post("api/reserver", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.lagOrdre(ctx);
                ctx.redirect("/index");
            }
        });
        app.post("api/loggInn", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.loggInn(ctx);
                ctx.redirect("/index");
            }
        });
        app.post("api/redirectBruker", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.settOppBrukerinfo(ctx);
                ctx.redirect("/bruker");
            }
        });
        app.post("/api/kortnummer/rediger", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.redigerKortnr(ctx);
                ctx.redirect("/bruker");
            }
        });
        app.post("/api/regnr/leggTil", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.leggTilRegnr(ctx);
                ctx.redirect("/bruker");
            }
        });
        app.post("/api/regnr/slett", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.slettRegnr(ctx);
                ctx.redirect("/bruker");
            }
        });
        app.post("/api/ordre/fjern", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.slettOrdre(ctx);
                ctx.redirect("/bruker");
            }
        });
        app.post("/api/parkeringsplass/fjern", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.slettParkeringsplass(ctx);
                ctx.redirect("/bruker");
            }
        });
        app.post("/api/parkeringsplass/fjernAdmin", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                controller.slettParkeringsplass(ctx);
                ctx.redirect("/administrator");
            }
        });
        app.post("/api/administrator", new Handler() {
            @Override
            public void handle(@NotNull Context ctx) throws Exception {
                ctx.redirect("/administrator");
            }
        });
    }
}
