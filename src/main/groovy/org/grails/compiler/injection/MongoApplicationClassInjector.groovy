package org.grails.compiler.injection

import grails.compiler.ast.AstTransformer
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.control.SourceUnit
import org.springframework.util.ClassUtils

/**
 * This transformation will exclude the org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
 * from the applications auto configuration
 *
 * @author James Kleeh
 */
@CompileStatic
@AstTransformer
class MongoApplicationClassInjector extends ApplicationClassInjector {

    private static final List<Integer> transformedInstances = []

    @Override
    void performInjectionOnAnnotatedClass(SourceUnit source, ClassNode classNode) {
        if(applicationArtefactHandler.isArtefact(classNode)) {
            def objectId = Integer.valueOf(System.identityHashCode(classNode))
            if (!transformedInstances.contains(objectId)) {
                transformedInstances << objectId
                def classLoader = getClass().classLoader
                final String enableAutoConfigurationClassName = "org.springframework.boot.autoconfigure.EnableAutoConfiguration"
                if(ClassUtils.isPresent(enableAutoConfigurationClassName, classLoader) ) {
                    def enableAutoConfigurationAnnotation = GrailsASTUtils.addAnnotationOrGetExisting(classNode, ClassHelper.make(classLoader.loadClass(enableAutoConfigurationClassName)))
                    final String excludeMongoClassName = "org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration"
                    if(ClassUtils.isPresent(excludeMongoClassName, classLoader)) {
                        def autoConfigClassExpression = new ClassExpression(ClassHelper.make(classLoader.loadClass(excludeMongoClassName)))
                        GrailsASTUtils.addExpressionToAnnotationMember(enableAutoConfigurationAnnotation, EXCLUDE_MEMBER, autoConfigClassExpression)
                    }
                }
            }
        }
    }
}
