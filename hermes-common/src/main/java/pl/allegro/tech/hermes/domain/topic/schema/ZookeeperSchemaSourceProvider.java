package pl.allegro.tech.hermes.domain.topic.schema;

import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import pl.allegro.tech.hermes.api.SchemaSource;
import pl.allegro.tech.hermes.api.Topic;
import pl.allegro.tech.hermes.common.exception.InternalProcessingException;
import pl.allegro.tech.hermes.infrastructure.zookeeper.ZookeeperPaths;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static pl.allegro.tech.hermes.domain.topic.schema.SchemaVersion.valueOf;

public class ZookeeperSchemaSourceProvider implements SchemaSourceProvider {

    protected static final String SCHEMA_SUFFIX = "schema";

    protected final CuratorFramework curatorFramework;
    protected final ZookeeperPaths zkPaths;

    public ZookeeperSchemaSourceProvider(CuratorFramework curatorFramework, ZookeeperPaths zkPaths) {
        this.curatorFramework = curatorFramework;
        this.zkPaths = zkPaths;
    }

    @Override
    public Optional<SchemaSource> get(Topic topic) {
        try {
            byte[] schemaBytes = curatorFramework.getData().forPath(zkPaths.topicPath(topic.getName(), SCHEMA_SUFFIX));
            return Optional.of(SchemaSource.valueOf(new String(schemaBytes, Charset.defaultCharset())));
        } catch (KeeperException.NoNodeException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            throw new InternalProcessingException("Could not load from zookeeper schema for topic " + topic.getQualifiedName(), ex);
        }
    }

    @Override
    public Optional<SchemaSource> get(Topic topic, SchemaVersion version) {
        return get(topic);
    }

    @Override
    public List<SchemaVersion> versions(Topic topic) {
        return ImmutableList.of(valueOf(0));
    }
}
