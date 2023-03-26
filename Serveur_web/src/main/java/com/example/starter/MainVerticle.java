package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import static io.vertx.core.Vertx.vertx;

public class MainVerticle extends AbstractVerticle {
  private JsonArray dictionariesJSON;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    VertxOptions vertxOptions = new VertxOptions();
    vertxOptions.setBlockedThreadCheckInterval(120000);

    /* Création du serveur web */
    var serverWeb = vertx(vertxOptions).createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x! YOYOYOYO");
    });



    dictionariesJSON = getInfoOnDictionaries();

    var router = Router.router(vertx);

    router.route(HttpMethod.GET, "/dictionaries").handler(req -> {

      req.response()
        .putHeader("content-type","application/json; charset=UTF8")
        .end(dictionariesJSON.encodePrettily());

    });

    router.route(HttpMethod.GET, "/game/:dictionary/:n").handler(req -> {

      String dictionary = req.pathParam("dictionary");
      String maxLengthString = req.pathParam("n");

      try{
        int maxLength = Integer.parseInt(maxLengthString);
        String chosenDictionaryPath=isDictionaryInDictionaries(dictionary);

        /* Check if the dictionary is a available dictionary */
        if(!chosenDictionaryPath.equals("")){

          /* Generate an anagram sequence */
          Game game = new Game(maxLength, chosenDictionaryPath);

          if(!game.isAnagramSequenceEmpty()){

            JsonArray anagramSequence = game.getAnagramSequenceAsJSON();

            req.response()
              .putHeader("content-type","application/json; charset=UTF8")
              .end(anagramSequence.encodePrettily());

          }else{

            req.response()
              .putHeader("content-type", "text/plain")
              .end("Could not generate anagram sequence");

          }

        }else{

          req.response()
            .putHeader("content-type", "text/plain")
            .end("Could not find dictionary");

        }

      }catch (NumberFormatException e){
        System.err.println("/game/"+dictionary+"/"+maxLengthString+" : n not a number");
      }

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

  private String isDictionaryInDictionaries(String dictionary){
    for(int i=0; i<dictionariesJSON.size(); i++){
      if (dictionariesJSON.getJsonObject(i).getString("language").equals(dictionary)){
        return dictionariesJSON.getJsonObject(i).getString("file");
      }
    }
    return "";
  }

  private JsonArray getInfoOnDictionaries(){

    /* finding all directories and associate each with the corresponding txt file in it
     * Create a JSON file with objects corresponding to dictionaries with "language" the dictionary name
     * and "file" the txt file in the directory
     */

    String dictionariesPath = "src/main/resources/dictionaries";
    File file = new File(dictionariesPath);
    String[] directories = file.list(new FilenameFilter() {
      @Override
      public boolean accept(File current, String name) {
        return new File(current, name).isDirectory();
      }
    });

    JsonArray dictionariesJA = new JsonArray();

    for(String dictionary : directories){

      JsonObject dictionaryJO = new JsonObject();
      dictionaryJO.put("language",dictionary);

      File folder = new File(dictionariesPath +"/"+dictionary);
      File[] listOfFiles = folder.listFiles();

      dictionaryJO.put("file", listOfFiles[0].toString());

      dictionariesJA.add(dictionaryJO);
    }

    return dictionariesJA;
  }


}
