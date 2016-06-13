package test.embedded.mongo

import grails.boot.*
import grails.boot.config.GrailsAutoConfiguration
import grails.plugins.metadata.*
import org.springframework.boot.autoconfigure.EnableAutoConfiguration

@PluginSource
@EnableAutoConfiguration(exclude=[org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration.class, org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.class])
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}