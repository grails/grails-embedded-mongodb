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
                new PropertySourcesConfig([grails: [mongodb: [version: "4.0.2"]]])
            }
        }

        expect:
        new EmbeddedMongoDBGrailsPlugin(grailsApplication: grailsApplication).getVersion() == Version.V4_0_2
    }

    void "test version release candidate"() {
        given:
        GrailsApplication grailsApplication = Mock(GrailsApplication) {
            1 * getConfig() >> {
                new PropertySourcesConfig([grails: [mongodb: [version: "3.2.1-rc3"]]])
            }
        }

        expect:
        new EmbeddedMongoDBGrailsPlugin(grailsApplication: grailsApplication).getVersion() == Version.V3_2_1_RC3
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
