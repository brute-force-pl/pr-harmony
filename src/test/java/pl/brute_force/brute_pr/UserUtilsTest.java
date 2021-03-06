package pl.brute_force.brute_pr;

import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.bitbucket.util.Page;
import com.atlassian.bitbucket.util.PageRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static pl.brute_force.brute_pr.TestUtils.mockApplicationUser;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserUtilsTest {

  @Mock
  private UserService userService;
  @InjectMocks
  UserUtils sut;

  @Before
  @SuppressWarnings("unchecked")
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    ApplicationUser userA = mockApplicationUser("userA");
    ApplicationUser userB = mockApplicationUser("userB");
    ApplicationUser user1 = mockApplicationUser("user1");
    ApplicationUser user2 = mockApplicationUser("user2");
    ApplicationUser user3 = mockApplicationUser("user3");
    ApplicationUser user4 = mockApplicationUser("user4");
    List<ApplicationUser> userList1 = newArrayList(user1, user2);
    List<ApplicationUser> userList2 = newArrayList(user3, user4);

    Page p1 = mock(Page.class);
    Page p2 = mock(Page.class);
    Page empty = mock(Page.class);
    when(p1.getValues()).thenReturn(userList1);
    when(p2.getValues()).thenReturn(userList2);
    when(empty.getValues()).thenReturn(emptyList());
    when(userService.findUsersByGroup(any(String.class), any(PageRequest.class))).then((Answer<Page>) invocation -> {
      String group = (String) invocation.getArguments()[0];
      PageRequest pageRequest = (PageRequest) invocation.getArguments()[1];

      if("group1".equals(group) && pageRequest.getStart() == 0) {
        return p1;
      } else if("group2".equals(group) && pageRequest.getStart() == 0) {
        return p2;
      } else {
        return empty;
      }
    });

    when(userService.getUserBySlug("userA")).thenReturn(userA);
    when(userService.getUserBySlug("userB")).thenReturn(userB);
    when(userService.getUserBySlug("user1")).thenReturn(user1);
    when(userService.getUserBySlug("user2")).thenReturn(user2);
    when(userService.getUserBySlug("user3")).thenReturn(user3);
    when(userService.getUserBySlug("user4")).thenReturn(user4);
  }

  @Test
  public void testDereferenceGroups_single() throws Exception {
    List<String> result = sut.dereferenceGroups(newArrayList("group1"));
    assertThat(result, is(notNullValue()));
    assertThat(result.size(), equalTo(2));
    assertThat(result, hasItems("user1", "user2"));
  }

  @Test
  public void testDereferenceGroups_multiple() throws Exception {
    List<String> result = sut.dereferenceGroups(newArrayList("group1", "group2"));
    assertThat(result, is(notNullValue()));
    assertThat(result.size(), equalTo(4));
    assertThat(result, hasItems("user1", "user2", "user3", "user4"));
  }
}
