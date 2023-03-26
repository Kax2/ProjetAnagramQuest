package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.pebble.PebbleTemplateEngine;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class MainVerticle extends AbstractVerticle {

  private JsonArray dictionariesJSON = getInfoOnDictionaries();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    /* Création du serveur web */
    var serverWeb = vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x! YOYOYOYO");
    });

    // 2: Création du routeur
    var router = Router.router(vertx);

    // Activation des sessions (pour enregistrer le nombre des visiteurs)
    //SessionStore sessionStore = LocalSessionStore.create(vertx);
    //router.route().handler(SessionHandler.create(sessionStore));

    // Initialisation du moteur de template
    final TemplateEngine engine = PebbleTemplateEngine.create(vertx);

    final StaticHandler staticHandlerRules = StaticHandler.create().setWebRoot("webroot/rules.html");
    final StaticHandler staticHandlerExamples = StaticHandler.create().setWebRoot("webroot/examples.html");

    router.route("/").handler(req -> {
      req.redirect("/rules/");
    });

    router.route("/rules/").handler(staticHandlerRules);
    router.route("/examples/").handler(staticHandlerExamples);


    router.route(HttpMethod.GET, "/dictionaries").handler(req -> {
      req.response()
        .putHeader("content-type","application/json; charset=UTF-8")
        .end(dictionariesJSON.encodePrettily());
    });


    router.route("/rules").handler(req -> {
      req.response()
        .putHeader("content-type","text/plain; charset=UTF-8")
        .end("Règles du Jeu");
    });

    router.route("/examples").handler(req -> {
      req.response()
        .putHeader("content-type","text/plain; charset=UTF-8")
        .end("Exemples d'anagrammes");
    });


    serverWeb.requestHandler(router);

    /* Attache du serveur sur le port 8888 */
    serverWeb.listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private JsonArray getInfoOnDictionaries(){

    /* finding all directories and associate each with the corresponding txt file in it
     * Create a JSON file with objects corresponding to dictionaries with "language" the dictionary name
     * and "file" the txt file in the directory
     */

    File file = new File("src/main/resources/dictionaries");
    String[] directories = file.list(new FilenameFilter() {
      @Override
      public boolean accept(File current, String name) {
        return new File(current, name).isDirectory();
      }
    });

    System.out.println(Arrays.toString(directories));

    JsonArray dictionariesJA = new JsonArray();

    for(String dictionary : directories){

      JsonObject dictionaryJO = new JsonObject();
      dictionaryJO.put("language",dictionary);

      dictionariesJA.add(dictionaryJO);
    }

    return dictionariesJA;
  }


}
