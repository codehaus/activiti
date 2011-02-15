package org.activiti.cycle.impl.db.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.activiti.cycle.impl.ActivitiCycleDbAwareTest;
import org.activiti.cycle.impl.CycleTagContentImpl;
import org.activiti.cycle.impl.db.entity.CycleConfigEntity;
import org.activiti.cycle.impl.db.entity.ProcessSolutionEntity;
import org.activiti.cycle.impl.db.entity.RepositoryArtifactLinkEntity;
import org.activiti.cycle.impl.db.entity.RepositoryNodeCommentEntity;
import org.activiti.cycle.impl.db.entity.RepositoryNodePeopleLinkEntity;
import org.activiti.cycle.impl.db.entity.RepositoryNodeTagEntity;
import org.activiti.cycle.impl.db.entity.VirtualRepositoryFolderEntity;
import org.activiti.cycle.processsolution.ProcessSolutionState;

import com.sun.org.apache.bcel.internal.generic.ASTORE;

public class CycleDaoMyBatisImplTest extends ActivitiCycleDbAwareTest {

  private CycleDaoMyBatisImpl dao = new CycleDaoMyBatisImpl();

  public void testArtifactLinks() throws Throwable {
    RepositoryArtifactLinkEntity link = new RepositoryArtifactLinkEntity();
    link.setSourceConnectorId("connector1");
    link.setSourceArtifactId("artifact1");
    link.setTargetConnectorId("connector2");
    link.setTargetArtifactId("artifact2");
    link.setLinkType("TEST");
    link.setComment("Created in test case");
    link.setLinkedBothWays(true);

    dao.insertArtifactLink(link);

    List<RepositoryArtifactLinkEntity> links1 = dao.getOutgoingArtifactLinks("connector1", "artifact1");
    assertEquals(1, links1.size());
    assertEquals("Created in test case", links1.get(0).getComment());
    assertNull(links1.get(0).getSourceRevision());
    assertNotNull(links1.get(0).getId());

    List<RepositoryArtifactLinkEntity> links2 = dao.getOutgoingArtifactLinks("connector2", "artifact2");
    assertEquals(0, links2.size());

    List<RepositoryArtifactLinkEntity> links3 = dao.getIncomingArtifactLinks("connector1", "artifact1");
    assertEquals(0, links3.size());

    List<RepositoryArtifactLinkEntity> links4 = dao.getIncomingArtifactLinks("connector2", "artifact2");
    assertEquals(1, links4.size());
    assertEquals("Created in test case", links4.get(0).getComment());
    assertEquals(links1.get(0).getId(), links4.get(0).getId());

    dao.deleteArtifactLink(links1.get(0).getId());

    List<RepositoryArtifactLinkEntity> links5 = dao.getIncomingArtifactLinks("connector2", "artifact2");
    assertEquals(0, links5.size());
  }

  public void testPeopleLink() {
    RepositoryNodePeopleLinkEntity link = new RepositoryNodePeopleLinkEntity();
    link.setSourceConnectorId("connector1");
    link.setSourceArtifactId("artifact1");
    link.setLinkType("TEST");
    link.setComment("Created in test case");
    link.setUserId("bernd");

    dao.insertPeopleLink(link);

    List<RepositoryNodePeopleLinkEntity> links1 = dao.getPeopleLinks("connector1", "artifact1");
    assertEquals(1, links1.size());
    assertEquals("Created in test case", links1.get(0).getComment());
    assertNotNull(links1.get(0).getId());

    List<RepositoryNodePeopleLinkEntity> links2 = dao.getPeopleLinks("connector2", "artifact2");
    assertEquals(0, links2.size());

    dao.deletePeopleLink(links1.get(0).getId());

    List<RepositoryNodePeopleLinkEntity> links3 = dao.getPeopleLinks("connector2", "artifact2");
    assertEquals(0, links3.size());
  }

  public void testTag() {
    RepositoryNodeTagEntity tag = new RepositoryNodeTagEntity("name1", "connector1", "artifact1");
    dao.insertTag(tag);

    List<RepositoryNodeTagEntity> tags1 = dao.getTagsForNode("connector1", "artifact1");
    assertEquals(1, tags1.size());
    assertEquals("name1", tags1.get(0).getName());
    assertNotNull(tags1.get(0).getId());

    List<RepositoryNodeTagEntity> tags2 = dao.getTagsForNode("connector2", "artifact2");
    assertEquals(0, tags2.size());

    CycleTagContentImpl tagContent1 = dao.getTagContent("name1");
    assertEquals("name1", tagContent1.getName());
    assertEquals(1, tagContent1.getUsageCount());

    RepositoryNodeTagEntity tag2 = new RepositoryNodeTagEntity("name1", "connector1", "artifact2");
    dao.insertTag(tag2);
    RepositoryNodeTagEntity tag3 = new RepositoryNodeTagEntity("name2", "connector1", "artifact2");
    dao.insertTag(tag3);

    CycleTagContentImpl tagContent2 = dao.getTagContent("name1");
    assertEquals("name1", tagContent2.getName());
    assertEquals(2, tagContent2.getUsageCount());

    List<CycleTagContentImpl> tagsGroupedByName = dao.getTagsGroupedByName();
    assertEquals("name1", tagsGroupedByName.get(0).getName());
    assertEquals(2, tagsGroupedByName.get(0).getUsageCount());
    assertEquals("name2", tagsGroupedByName.get(1).getName());
    assertEquals(1, tagsGroupedByName.get(1).getUsageCount());

    dao.deleteTag("connector1", "artifact1", "name1");
    dao.deleteTag("connector1", "artifact2", "name1");
    dao.deleteTag("connector1", "artifact2", "name2");

    List<RepositoryNodeTagEntity> tags3 = dao.getTagsForNode("connector1", "artifact1");
    assertEquals(0, tags3.size());

    // List<String> similiarTagNames = dao.getSimiliarTagNames("ame");
    // assertEquals(2, similiarTagNames.size());
    // assertEquals("name1", similiarTagNames.get(0));
    // assertEquals("name2", similiarTagNames.get(1));
  }

  public void testTagFindSimiliar() {
    RepositoryNodeTagEntity tag = new RepositoryNodeTagEntity("name1", "connector1", "artifact1");
    dao.insertTag(tag);

    List<RepositoryNodeTagEntity> tags1 = dao.getTagsForNode("connector1", "artifact1");
    assertEquals(1, tags1.size());
    assertEquals("name1", tags1.get(0).getName());
    assertNotNull(tags1.get(0).getId());

    List<RepositoryNodeTagEntity> tags2 = dao.getTagsForNode("connector2", "artifact2");
    assertEquals(0, tags2.size());

    CycleTagContentImpl tagContent1 = dao.getTagContent("name1");
    assertEquals("name1", tagContent1.getName());
    assertEquals(1, tagContent1.getUsageCount());

    RepositoryNodeTagEntity tag2 = new RepositoryNodeTagEntity("name1", "connector1", "artifact2");
    dao.insertTag(tag2);
    RepositoryNodeTagEntity tag3 = new RepositoryNodeTagEntity("name2", "connector1", "artifact2");
    dao.insertTag(tag3);

    CycleTagContentImpl tagContent2 = dao.getTagContent("name1");
    assertEquals("name1", tagContent2.getName());
    assertEquals(2, tagContent2.getUsageCount());

    List<CycleTagContentImpl> tagsGroupedByName = dao.getTagsGroupedByName();
    assertEquals("name1", tagsGroupedByName.get(0).getName());
    assertEquals(2, tagsGroupedByName.get(0).getUsageCount());
    assertEquals("name2", tagsGroupedByName.get(1).getName());
    assertEquals(1, tagsGroupedByName.get(1).getUsageCount());

    dao.deleteTag("connector1", "artifact1", "name1");
    dao.deleteTag("connector1", "artifact2", "name1");
    dao.deleteTag("connector1", "artifact2", "name2");

    List<RepositoryNodeTagEntity> tags3 = dao.getTagsForNode("connector1", "artifact1");
    assertEquals(0, tags3.size());
  }

  public void testComment() {
    RepositoryNodeCommentEntity comment = new RepositoryNodeCommentEntity();
    String id = UUID.randomUUID().toString();
    comment.setId(id);
    comment.setAuthor("Me");
    comment.setConnectorId("testConnectorId");
    comment.setNodeId("testNodeId");
    comment.setContent("Test Comment...");
    comment.setDate(new Date());
    comment.setElementId("");
    dao.insertComment(comment);
    assertEquals(1, dao.getCommentsForNode("testConnectorId", "testNodeId").size());
    dao.deleteComment(id);
    assertEquals(0, dao.getCommentsForNode("testConnectorId", "testNodeId").size());
  }

  public void testSelectCycleConfigGroups() {
    CycleConfigEntity ce1 = new CycleConfigEntity();
    ce1.setGroupName("g1");
    ce1.setKey("key1");
    ce1.setValue("value1");

    CycleConfigEntity ce2 = new CycleConfigEntity();
    ce2.setGroupName("g2");
    ce2.setKey("key2");
    ce2.setValue("value2");

    CycleConfigEntity ce3 = new CycleConfigEntity();
    ce3.setGroupName("g1"); // g1
    ce3.setKey("key3");
    ce3.setValue("value3");

    dao.saveCycleConfig(ce1);
    dao.saveCycleConfig(ce2);
    dao.saveCycleConfig(ce3);

    List<String> groups = dao.selectCycleConfigurationGroups();

    assertEquals(2, groups.size());

    for (String group : groups) {
      for (CycleConfigEntity entity : dao.selectCycleConfigByGroup(group)) {
        dao.deleteCycleConfigurationEntry(entity.getId());
      }
    }
  }

  public void testCreateDeleteProcessSolution() {
    ProcessSolutionEntity ps = new ProcessSolutionEntity();
    ps.setLabel("My Process Solution");
    ps.setState(ProcessSolutionState.IN_SPECIFICATION);

    Assert.assertNull(ps.getId());
    ps = dao.saveProcessSolution(ps);
    Assert.assertNotNull(ps.getId());
    ProcessSolutionEntity ps2 = dao.getProcessSolutionById(ps.getId());
    Assert.assertEquals(ps.getId(), ps2.getId());
    Assert.assertEquals(ps.getLabel(), ps2.getLabel());
    Assert.assertEquals(ps.getState(), ps2.getState());

    assertTrue(dao.getProcessSolutionList().size() == 1);

    dao.deleteProcessSolutionById(ps.getId());
    Assert.assertNull(dao.getProcessSolutionById(ps.getId()));
  }

  public void testCreateDeleteRepositoryFolder() {
    VirtualRepositoryFolderEntity vf1 = new VirtualRepositoryFolderEntity();
    vf1.setConnectorId("vfs");
    vf1.setLabel("Management");
    vf1.setReferencedNodeId("/ps1/Management");
    vf1.setType("Management");
    VirtualRepositoryFolderEntity vf2 = new VirtualRepositoryFolderEntity();
    vf2.setConnectorId("vfs");
    vf2.setLabel("Requirements");
    vf2.setReferencedNodeId("/ps1/Requirements");
    vf2.setType("Requirements");

    ProcessSolutionEntity ps = new ProcessSolutionEntity();
    ps.setLabel("My Process Solution");
    ps.setState(ProcessSolutionState.IN_SPECIFICATION);
    ps = dao.saveProcessSolution(ps);

    List<VirtualRepositoryFolderEntity> folders = new ArrayList<VirtualRepositoryFolderEntity>();
    folders.add(vf1);
    folders.add(vf2);

    folders = dao.addVirtualFoldersToSolution(ps.getId(), folders);
    for (VirtualRepositoryFolderEntity virtualRepositoryFolderEntity : folders) {
      VirtualRepositoryFolderEntity storedFolder = dao.getVirtualRepositoryFolderById(virtualRepositoryFolderEntity.getId());
      assertNotNull(storedFolder);
      assertEquals(virtualRepositoryFolderEntity.getConnectorId(), storedFolder.getConnectorId());
      assertEquals(virtualRepositoryFolderEntity.getGlobalUniqueId(), storedFolder.getGlobalUniqueId());
      assertEquals(virtualRepositoryFolderEntity.getLabel(), storedFolder.getLabel());
      assertEquals(virtualRepositoryFolderEntity.getReferencedNodeId(), storedFolder.getReferencedNodeId());
      assertEquals(virtualRepositoryFolderEntity.getType(), storedFolder.getType());
      assertEquals(virtualRepositoryFolderEntity.getProcessSolutionId(), storedFolder.getProcessSolutionId());
      dao.deleteVirtualRepositoryFolderById(storedFolder.getId());
    }

    assertTrue(dao.getVirtualForldersByProcessSolutionId(ps.getId()).size() == 0);

    dao.deleteProcessSolutionById(ps.getId());

  }

}
