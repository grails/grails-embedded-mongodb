package org.grails.plugin.embedded.mongodb

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import grails.config.Config
import grails.core.GrailsApplication
import org.grails.datastore.mapping.mongo.MongoDatastore
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

/**
 * This class will clear all non system collections from the running mongo database
 * before each test method is executed
 *
 * @author James Kleeh
 */
class MongoTestExeuctionListener implements TestExecutionListener {

    static MongoDatabase db

    @Override
    void beforeTestClass(TestContext testContext) throws Exception {
        if (!db) {
            MongoClient mongo = testContext.applicationContext.getBean("mongo", MongoClient)
            Config config = (Config)testContext.applicationContext.getBean("grailsApplication", GrailsApplication).config
            String databaseName = config.getProperty(MongoDatastore.SETTING_DATABASE_NAME, "")
            db = mongo.getDatabase(databaseName)
        }
    }

    @Override
    void prepareTestInstance(TestContext testContext) throws Exception {

    }

    @Override
    void beforeTestMethod(TestContext testContext) throws Exception {
        db.collectionNames
            .findAll { !it.startsWith("system") }
            .each { db.getCollection(it).drop() }
    }

    @Override
    void afterTestMethod(TestContext testContext) throws Exception {

    }

    @Override
    void afterTestClass(TestContext testContext) throws Exception {

    }
}
