package fortscale.services.impl;

import fortscale.domain.core.AlertTimeframe;
import fortscale.domain.core.Tag;
import fortscale.domain.core.dao.TagRepository;

import fortscale.services.UserService;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
//import org.mockito.ArgumentCaptor;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by shays on 20/02/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    TagServiceImpl tagService;


    @Test
    public void deleteNotExistingTagTest(){
        String tagName = "admini";
        Tag tag = new Tag(tagName);

        boolean success=tagService.deleteTag(tagName);
        assertFalse(success);
    }


    @Test
    public void deleteTagTest(){
        String tagName = "admini";
        Tag tag = new Tag(tagName);
        when(tagRepository.findByNameIgnoreCase(tagName)).thenReturn(tag);

        assertEquals(false, tag.getDeleted());
        boolean success=tagService.deleteTag(tagName);
        assertTrue(success);

        //Check that tag value changed
        assertEquals(true, tag.getDeleted());

        //Check the tag changed saved into DB
        ArgumentCaptor<Tag> updatedTagParam = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository, times(1)).updateTag(updatedTagParam.capture());
        assertEquals(tagName,updatedTagParam.getValue().getName());
        assertEquals(true,updatedTagParam.getValue().getDeleted());

        //Check that all users which has the deleted tag, lost it
        verify(userService, times(1)).removeTagFromAllUsers(tagName);

    }

    @Test
    public void testUpdateTag(){
        ReflectionTestUtils.setField(tagService, "maxTagLength", 100);

        String tagName = "admini";
        String newTagName = "new-admin";

        Tag updatedTag = new Tag(tagName);
        updatedTag.setDisplayName(newTagName);


        boolean updateSuccess = tagService.updateTag(updatedTag);
        assertTrue(updateSuccess);
        ArgumentCaptor<Tag> updatedTagParam = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository, times(1)).updateTag(updatedTagParam.capture());
        updatedTagParam.getValue().getDisplayName().equals(newTagName);

    }

    @Test
    public void testUpdateDeletedTag(){
        ReflectionTestUtils.setField(tagService, "maxTagLength", 100);

        String tagName = "admini";
        String newTagName = "new-admin";

        Tag updatedTag = new Tag(tagName);
        updatedTag.setDisplayName(newTagName);
        updatedTag.setDeleted(true);

        boolean updateSuccess = tagService.updateTag(updatedTag);
        assertFalse(updateSuccess);
        verify(tagRepository, times(0)).updateTag(any(Tag.class));




    }

}
