package org.grails.plugin.embedded.mongodb

import com.mongodb.ServerAddress
import de.flapdoodle.embed.mongo.distribution.Version
import grails.core.GrailsApplication
import org.grails.config.PropertySourcesConfig
import spock.lang.Specification

/**
 * Created by Jim on 6/15/2016.
 */
class EmbeddedMongoDBGrailsPluginSpec extends Specification {

    void "test version no config"() {
        given:
        GrailsApplication grailsApplication = Mock(GrailsApplication) {
            1 * getConfig() >> {
                new PropertySourcesConfig([:])
            }
        }

        expect:
        new EmbeddedMongoDBGrailsPlugin(grailsApplication: grailsApplication).getVersion().asInDownloadPath() == Version.Main.PRODUCTION.asInDownloadPath()
    }

    void "test version"() {
        given:
        GrailsApplication grailsApplication = Mock(GrailsApplication) {
            1 * getConfig() >> {
                new PropertySourcesConfig([grails: [mongodb: [version: "3.4.1"]]])
            }
        }

        expect:
        new EmbeddedMongoDBGrailsPlugin(grailsApplication: grailsApplication).getVersion() == Version.V3_4_1
    }

    void "test version release candidate"() {
        given:
        GrailsApplication grailsApplication = Mock(GrailsApplication) {
            1 * getConfig() >> {
                new PropertySourcesConfig([grails: [mongodb: [version: "3.5.1"]]])
            }
        }

        expect:
        new EmbeddedMongoDBGrailsPlugin(grailsApplication: grailsApplication).getVersion() == Version.V3_5_1
    }

    void "test version not found"() {
        given:
        GrailsApplication grailsApplication = Mock(GrailsApplication) {
            1 * getConfig() >> {
                new PropertySourcesConfig([grails: [mongodb: [version: "32"]]])
            }
        }

        when:
        new EmbeddedMongoDBGrailsPlugin(grailsApplication: grailsApplication).getVersion()

        then:
        thrown(IllegalArgumentException)
    }

    void "test default port"() {
        given:
        GrailsApplication grailsApplication = Mock(GrailsApplication) {
            1 * getConfig() >> {
                new PropertySourcesConfig([:])
            }
        }

        expect:
        new EmbeddedMongoDBGrailsPlugin(grailsApplication: grailsApplication).getPort() == ServerAddress.defaultPort()
    }

    void "test port"() {
        given:
        GrailsApplication grailsApplication = Mock(GrailsApplication) {
            1 * getConfig() >> {
                new PropertySourcesConfig([grails: [mongodb: [port: "32"]]])
            }
        }

        expect:
        new EmbeddedMongoDBGrailsPlugin(grailsApplication: grailsApplication).getPort() == 32
    }
}
