

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.webresources.DirResourceSet;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        String webappDirLocation = "src/main/webapp/";

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir("target/tomcat-work"); // carpeta de trabajo temporal
        tomcat.getConnector(); // fuerza la creación del conector

        // Contexto apuntando directamente a tu carpeta fuente (no al target/war)
        Context ctx = tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        ctx.setReloadable(true); // recarga JSPs/recursos al cambiar

        // Hace que las clases compiladas en target/classes se vean como parte del contexto
        File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        tomcat.start();
        System.out.println("Servidor arriba en http://localhost:8080/");
        tomcat.getServer().await();
    }
}