package org.grails.plugin.embedded.mongodb

import com.mongodb.ServerAddress
import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.config.IRuntimeConfig
import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.runtime.Network
import grails.plugins.*
import grails.util.Environment
import org.grails.datastore.mapping.mongo.MongoDatastore

class EmbeddedMongoDBGrailsPlugin extends Plugin {

    String grailsVersion = '3.1.0 > *'
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
        config.getProperty(MongoDatastore.SETTING_PORT, int, ServerAddress.defaultPort())
    }

    IFeatureAwareVersion getVersion() {
        String version = config.getProperty("grails.mongodb.version", String, Version.Main.PRODUCTION.asInDownloadPath())
        Version.valueOf("V" + version.replaceAll(/(\.|-)/, '_').toUpperCase())
    }

    static MongodExecutable mongodExecutable = null

    Closure doWithSpring() {{->
        if (Environment.current == Environment.TEST) {
            IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                    .defaults(Command.MongoD)
                    .processOutput(ProcessOutput.defaultInstanceSilent)
                    .build()

            MongodStarter starter = MongodStarter.getInstance(runtimeConfig)

            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(getVersion())
                    .net(new Net("127.0.0.1", getPort(), Network.localhostIsIPv6()))
                    .build()

            mongodExecutable = starter.prepare(mongodConfig)
            mongodExecutable.start()
        }
    }}

    void onShutdown(evt) {
        if (mongodExecutable != null) {
            mongodExecutable.stop()
        }
    }

}
