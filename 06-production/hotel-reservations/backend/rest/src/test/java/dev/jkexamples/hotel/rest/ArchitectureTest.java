package dev.jkexamples.hotel.rest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rules a Spring team expects to read as ArchUnit tests. The module boundaries themselves (rest sees
 * contract and service only) are enforced by jk-guards.toml before any test runs; these rules are
 * the same idea at class level, and the overlap is a cross-check.
 */
@AnalyzeClasses(packages = "dev.jkexamples.hotel", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule controllers_are_named_for_what_they_are = classes()
            .that().areAnnotatedWith(RestController.class)
            .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule transactions_are_opened_by_services_only = noClasses()
            .that().resideOutsideOfPackage("dev.jkexamples.hotel.service..")
            .should().beAnnotatedWith(Transactional.class);

    @ArchTest
    static final ArchRule controllers_never_touch_the_domain_or_the_database = noClasses()
            .that().resideInAPackage("dev.jkexamples.hotel.rest..")
            .should().dependOnClassesThat().resideInAnyPackage("dev.jkexamples.hotel.core..", "org.jooq..", "dev.jkexamples.hotel.service.internal..");

    @ArchTest
    static final ArchRule the_domain_depends_on_nothing_but_the_jdk = noClasses()
            .that().resideInAPackage("dev.jkexamples.hotel.core..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "org.jooq..", "jakarta..");

    @ArchTest
    static final ArchRule packages_are_free_of_cycles = slices()
            .matching("dev.jkexamples.hotel.(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule no_field_injection = noFields()
            .should().beAnnotatedWith(Autowired.class)
            .because("constructor injection keeps a bean's dependencies final and visible");
}
