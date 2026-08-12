package local.socialnetwork.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.ClassFileImporter;

import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Documents and enforces the intended module boundaries between features
 * (auth, profiles, posts, comments, likes) and infrastructure packages (core, shared).
 * <p>
 * Note: {@code onlyBeAccessed()} only catches calls to members declared directly on a
 * matched class. Calls to inherited members (e.g. {@code CrudRepository} methods like
 * {@code findById}, or getters inherited from {@code AbstractBaseModel}) resolve to the
 * declaring superclass and are not attributed to the feature package, so this rule does
 * not catch every cross-feature repository/entity access.
 */
class ModuleBoundaryTest {

    private static final String BASE_PACKAGE = "local.socialnetwork";
    private static final String[] FEATURES = {"auth", "profiles", "posts", "comments", "likes"};

    private static JavaClasses importProductionClasses() {
        return new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);
    }

    @Test
    void featuresDoNotAccessOtherFeaturesEntitiesOrRepositoriesDirectly() {
        JavaClasses classes = importProductionClasses();
        List<String> violations = new ArrayList<>();

        for (String feature : FEATURES) {
            ArchRule rule = classes()
                    .that().resideInAnyPackage(
                            BASE_PACKAGE + "." + feature + ".entity..",
                            BASE_PACKAGE + "." + feature + ".repository..")
                    .should().onlyBeAccessed().byAnyPackage(BASE_PACKAGE + "." + feature + "..");

            try {
                rule.check(classes);
            } catch (AssertionError e) {
                violations.add("Feature [" + feature + "]:" + System.lineSeparator() + e.getMessage());
            }
        }

        if (!violations.isEmpty()) {
            fail(String.join(System.lineSeparator() + System.lineSeparator(), violations));
        }
    }

    @Test
    void coreAndSharedDoNotDependOnFeaturePackages() {
        JavaClasses classes = importProductionClasses();

        ArchRule rule = noClasses()
                .that().resideInAnyPackage(BASE_PACKAGE + ".core..", BASE_PACKAGE + ".shared..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        BASE_PACKAGE + ".auth..",
                        BASE_PACKAGE + ".profiles..",
                        BASE_PACKAGE + ".posts..",
                        BASE_PACKAGE + ".comments..",
                        BASE_PACKAGE + ".likes..");

        rule.check(classes);
    }
}
