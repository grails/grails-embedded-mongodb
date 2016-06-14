package org.grails.plugin.embedded.mongodb

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import grails.config.Config
import grails.core.GrailsApplication
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class MongoTestExeuctionListener implements TestExecutionListener {

    static MongoDatabase db

    @Override
    void beforeTestClass(TestContext testContext) throws Exception {
        if (!db) {
            MongoClient mongo = testContext.applicationContext.getBean("mongo", MongoClient)
            Config config = (Config)testContext.applicationContext.getBean("grailsApplication", GrailsApplication).config
            String databaseName = config.getProperty("grails.mongodb.databaseName", "")
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
