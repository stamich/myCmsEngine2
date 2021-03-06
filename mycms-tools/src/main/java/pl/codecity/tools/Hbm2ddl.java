package pl.codecity.tools;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import pl.codecity.main.configuration.ExtendedMySQL5InnoDBDialect;
import pl.codecity.main.configuration.PhysicalNamingStrategySnakeCaseImpl;

import javax.persistence.Entity;
import java.util.EnumSet;

public class Hbm2ddl {

    public static void main(String[] args) throws Exception {
        String locationPattern = "classpath:/pl/codecity/main/model/*";

        final BootstrapServiceRegistry registry = new BootstrapServiceRegistryBuilder().build();
        final MetadataSources metadataSources = new MetadataSources(registry);
        final StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder(registry);

        registryBuilder.applySetting(AvailableSettings.DIALECT, ExtendedMySQL5InnoDBDialect.class.getCanonicalName());
        registryBuilder.applySetting(AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS, true);
        registryBuilder.applySetting(AvailableSettings.PHYSICAL_NAMING_STRATEGY, PhysicalNamingStrategySnakeCaseImpl.class);

        final PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        final Resource[] resources = resourcePatternResolver.getResources(locationPattern);
        final SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        for (Resource resource : resources) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
            AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
            if (metadata.hasAnnotation(Entity.class.getName())) {
                metadataSources.addAnnotatedClass(Class.forName(metadata.getClassName()));
            }
        }

        final StandardServiceRegistryImpl registryImpl = (StandardServiceRegistryImpl) registryBuilder.build();
        final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder(registryImpl);

        new SchemaExport()
                .setHaltOnError(true)
                .setDelimiter(";")
                .create(EnumSet.of(TargetType.STDOUT), metadataBuilder.build());
    }
}
