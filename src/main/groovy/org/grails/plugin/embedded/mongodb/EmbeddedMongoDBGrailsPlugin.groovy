package org.grails.plugin.embedded.mongodb

import com.mongodb.ServerAddress
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion
import de.flapdoodle.embed.mongo.distribution.Versions
import de.flapdoodle.embed.mongo.transitions.Mongod
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess
import de.flapdoodle.embed.process.io.ProcessOutput
import de.flapdoodle.reverse.TransitionWalker
import de.flapdoodle.reverse.transitions.Start
import grails.plugins.*
import grails.util.Environment
import org.grails.datastore.mapping.mongo.MongoDatastore
import de.flapdoodle.embed.process.distribution.Version

class EmbeddedMongoDBGrailsPlugin extends Plugin {

    String grailsVersion = '3.3.11 > *'
    String author = 'James Kleeh'
    String authorEmail = 'kleehj@ociweb.com'
    String title = 'Mongo Embedded Integration Test Plugin'
    String description = 'A plugin to provide an in memory mongo database to be used for integration testing'
    String documentation = 'http://grails-plugins.github.io/grails-embedded-mongodb/latest'
    String license = 'APACHE'
    def organization = [name: 'Grails', url: 'http://www.grails.org/']
    def issueManagement = [url: 'http://grails-plugins.github.io/grails-embedded-mongodb/issues']
    def scm = [url: 'https://github.com/grails-plugins/grails-embedded-mongodb']

    int getPort() {
        config.getProperty(MongoDatastore.SETTING_PORT, Integer, ServerAddress.defaultPort())
    }

    IFeatureAwareVersion getVersion() {
        String version = config.getProperty("grails.mongodb.version", String, de.flapdoodle.embed.mongo.distribution.Version.Main.V6_0.asInDownloadPath())
        Versions.withFeatures(Version.of(version))
    }

//    static MongodExecutable mongodExecutable = null
    TransitionWalker.ReachedState<RunningMongodProcess> started

    Closure doWithSpring() {{->
        if (Environment.current == Environment.TEST) {
            started = Mongod.instance()
                    .withProcessOutput(Start.to(ProcessOutput.class)
                            .providedBy(ProcessOutput::silent))
                    .withNet(Start.to(Net.class)
                            .initializedWith(Net.builder()
                                    .bindIp("127.0.0.1")
                                    .port(getPort())
                                    .isIpv6(de.flapdoodle.net.Net.localhostIsIPv6())
                                    .build()))
            .start(getVersion())
        }
    }}

    void onShutdown(evt) {
        if (started != null) {
            started.close()
        }
    }

}
