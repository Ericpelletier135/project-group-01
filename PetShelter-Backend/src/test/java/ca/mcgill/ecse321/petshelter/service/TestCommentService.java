package ca.mcgill.ecse321.petshelter.service;

import ca.mcgill.ecse321.petshelter.dto.CommentDTO;
import ca.mcgill.ecse321.petshelter.model.Comment;
import ca.mcgill.ecse321.petshelter.model.Forum;
import ca.mcgill.ecse321.petshelter.model.User;
import ca.mcgill.ecse321.petshelter.repository.CommentRepository;
import ca.mcgill.ecse321.petshelter.repository.ForumRepository;
import ca.mcgill.ecse321.petshelter.repository.UserRepository;
import ca.mcgill.ecse321.petshelter.service.exception.CommentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.sql.Date;
import java.sql.Time;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class TestCommentService {

	private static final long USER_ID = 13;
	private static final String USER_NAME = "TestPerson";
	private static final String USER_EMAIL = "TestPerson@email.com";
	private static final String USER_PASSWORD = "myP1+abc";

	private static final long COMMENT_ID = 42;
	private static final String COMMENT_TEXT = "My comment. Hewwo~!";
	private static final Date COMMENT_DATE = Date.valueOf("2006-12-30");
	private static final Time COMMENT_TIME = Time.valueOf("00:38:54");

	private static final long FORUM_ID = 343;
	private static final long FORUM_LOCKED_ID = 345;
	private static final String FORUM_TITLE = "New Forum. What's this~?";

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ForumRepository forumRepository;

	@InjectMocks
	private CommentService commentService;

	@BeforeEach
	public void setMockOutput() {
		MockitoAnnotations.initMocks(this);

		lenient().when(userRepository.findById(anyLong())).thenAnswer((InvocationOnMock invocation) -> {
			if (invocation.getArgument(0).equals(USER_ID)) {
				User user = new User();
				user.setId(USER_ID);
				user.setUserName(USER_NAME);
				user.setEmail(USER_EMAIL);
				user.setPassword(USER_PASSWORD);
				return Optional.of(user);
			} else {
				return Optional.empty();
			}
		});

		lenient().when(forumRepository.findById(anyLong())).thenAnswer((InvocationOnMock invocation) -> {
			if (invocation.getArgument(0).equals(FORUM_ID)) {
				User user = new User();
				user.setId(USER_ID);
				user.setUserName(USER_NAME);
				user.setEmail(USER_EMAIL);
				user.setPassword(USER_PASSWORD);
				Set<User> subscribers = new HashSet<>();
				subscribers.add(user);
				Comment comment = new Comment();
				comment.setTime(COMMENT_TIME);
				comment.setDatePosted(COMMENT_DATE);
				comment.setId(COMMENT_ID);
				comment.setText(COMMENT_TEXT);
				comment.setUser(user);
				Set<Comment> comments = new HashSet<>();
				comments.add(comment);
				Forum forum = new Forum();
				forum.setId(FORUM_ID);
				forum.setTitle(FORUM_TITLE);
				forum.setAuthor(user);
				forum.setComments(comments);
				forum.setLocked(false);
				forum.setSubscribers(subscribers);
				return Optional.of(forum);
			} else if (invocation.getArgument(0).equals(FORUM_LOCKED_ID)) {
				User user = new User();
				user.setId(USER_ID);
				user.setUserName(USER_NAME);
				user.setEmail(USER_EMAIL);
				user.setPassword(USER_PASSWORD);
				Set<User> subscribers = new HashSet<>();
				subscribers.add(user);
				Comment comment = new Comment();
				comment.setTime(COMMENT_TIME);
				comment.setDatePosted(COMMENT_DATE);
				comment.setId(COMMENT_ID);
				comment.setText(COMMENT_TEXT);
				comment.setUser(user);
				Set<Comment> comments = new HashSet<>();
				comments.add(comment);
				Forum forum = new Forum();
				forum.setId(FORUM_LOCKED_ID);
				forum.setTitle(FORUM_TITLE);
				forum.setAuthor(user);
				forum.setComments(comments);
				forum.setLocked(true);
				forum.setSubscribers(subscribers);
				return Optional.of(forum);
			} else {
				return Optional.empty();
			}
		});

		lenient().when(commentRepository.findById(anyLong())).thenAnswer((InvocationOnMock invocation) -> {
			if (invocation.getArgument(0).equals(COMMENT_ID)) {
				User user = new User();
				user.setId(USER_ID);
				user.setUserName(USER_NAME);
				user.setEmail(USER_EMAIL);
				user.setPassword(USER_PASSWORD);
				Comment comment = new Comment();
				comment.setTime(COMMENT_TIME);
				comment.setDatePosted(COMMENT_DATE);
				comment.setId(COMMENT_ID);
				comment.setText(COMMENT_TEXT);
				comment.setUser(user);
				return Optional.of(comment);
			}
			return Optional.empty();
		});

		lenient().when(commentRepository.findCommentsByUser(any(User.class)))
				.thenAnswer((InvocationOnMock invocation) -> {
					if (((User) invocation.getArgument(0)).getId() == (USER_ID)) {
						User user = new User();
						user.setId(USER_ID);
						user.setUserName(USER_NAME);
						user.setEmail(USER_EMAIL);
						user.setPassword(USER_PASSWORD);
						Comment comment = new Comment();
						comment.setTime(COMMENT_TIME);
						comment.setDatePosted(COMMENT_DATE);
						comment.setId(COMMENT_ID);
						comment.setText(COMMENT_TEXT);
						comment.setUser(user);
						List<Comment> comments = new ArrayList<>();
						comments.add(comment);
						return comments;
					}
					return new ArrayList<Comment>();
				});

		lenient().when(commentRepository.findAll()).thenAnswer((InvocationOnMock invocation) -> {
			User user = new User();
			user.setId(USER_ID);
			user.setUserName(USER_NAME);
			user.setEmail(USER_EMAIL);
			user.setPassword(USER_PASSWORD);
			Comment comment = new Comment();
			comment.setTime(COMMENT_TIME);
			comment.setDatePosted(COMMENT_DATE);
			comment.setId(COMMENT_ID);
			comment.setText(COMMENT_TEXT);
			comment.setUser(user);
			List<Comment> comments = new ArrayList<>();
			comments.add(comment);
			return comments;
		});

		// Set a reflexive return answer.
		Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> invocation.getArgument(0);
		lenient().when(commentRepository.save(any(Comment.class))).thenAnswer(returnParameterAsAnswer);
		lenient().when(forumRepository.save(any(Forum.class))).thenAnswer(returnParameterAsAnswer);

	}

	/**
	 * This test verifies the operation of addComment().
	 */
	@Test
	public void testAddComment() {
		CommentDTO comment;
		try {
			comment = commentService.addComment(COMMENT_TEXT, FORUM_ID, USER_ID);
			assertEquals(USER_NAME, comment.getUsername());
			assertEquals(0, comment.getId());
			assertEquals(COMMENT_TEXT, comment.getText());
		} catch (CommentException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * This test verifies that comment creation is prevented by the isLocked() flag.
	 */
	@Test
	public void testLockedThreadComment() {
		CommentException thrown = assertThrows(CommentException.class,
				() -> commentService.addComment(COMMENT_TEXT, FORUM_LOCKED_ID, USER_ID));
		assertTrue(thrown.getMessage().contains("Forum thread is locked."));
	}

	/**
	 * This test verifies that you need a valid forum to create a comment.
	 */
	@Test
	public void testMissingThreadComment() {
		CommentException thrown = assertThrows(CommentException.class,
				() -> commentService.addComment(COMMENT_TEXT, 0, USER_ID));
		assertTrue(thrown.getMessage().contains("No such forum thread."));
	}

	/**
	 * This test verifies that you need a valid user to create a comment.
	 */
	@Test
	public void testMissingUserComment() {
		CommentException thrown = assertThrows(CommentException.class,
				() -> commentService.addComment(COMMENT_TEXT, FORUM_ID, 0));
		assertTrue(thrown.getMessage().contains("No such user."));
	}

	/**
	 * This test verifies that comment update works.
	 */
	@Test
	public void testCommentUpdate() {
		String newText = "This is an updated comment text.";
		CommentDTO comment;
		try {
			comment = commentService.updateComment(COMMENT_ID, newText);
			assertEquals(newText, comment.getText());
			assertEquals(COMMENT_ID, comment.getId());
		} catch (CommentException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * This test verifies that an invalid comment ID results in an exception when
	 * updating.
	 */
	@Test
	public void testMissingCommentUpdate() {
		String newText = "This is an updated comment text.";
		CommentException thrown = assertThrows(CommentException.class, () -> commentService.updateComment(0, newText));
		assertTrue(thrown.getMessage().contains("No such comment."));
	}

	/**
	 * This test verify comment deletion works.
	 */
	@Test
	public void testCommentDelete() {
		CommentDTO comment;
		try {
			comment = commentService.deleteComment(COMMENT_ID);
			assertEquals(USER_NAME, comment.getUsername());
			assertEquals(COMMENT_ID, comment.getId());
			assertEquals(COMMENT_TEXT, comment.getText());
		} catch (CommentException e) {
			fail(e.getMessage());
		}

	}

	/**
	 * This test verifies that an invalid comment ID raises an exception when
	 * deleting.
	 */
	@Test
	public void testMissingCommentDelete() {
		CommentException thrown = assertThrows(CommentException.class, () -> commentService.deleteComment(0));
		assertTrue(thrown.getMessage().contains("No such comment."));
	}

	/**
	 * This test verifies get all comments works.
	 */
	@Test
	public void testGetAllComments() throws CommentException {
		Set<CommentDTO> comments = commentService.getComments(FORUM_ID);
		assertEquals(1, comments.size()); // List contains a single element.
		CommentDTO comment = (CommentDTO) comments.toArray()[0];
		assertEquals(COMMENT_ID, comment.getId()); // List contains the only comment.
	}

	/**
	 * This test verifies get all comments works.
	 */
	@Test
	public void testGetAllCommentsNoComments(){
		try {
			commentService.getComments(FORUM_ID + 1);
		} catch (CommentException e) {
			assertEquals(e.getMessage(),"No comments found");
		}
	}

	/**
	 * This test verifies get comments by user works.
	 */
	@Test
	public void testGetUserComments() {
		List<CommentDTO> comments;
		try {
			comments = commentService.getCommentsByUser(USER_ID);

			assertEquals(1, comments.size()); // List contains a single element.
			assertEquals(COMMENT_ID, comments.get(0).getId()); // List contains the correct comment.
			assertEquals(USER_NAME, comments.get(0).getUsername()); // Comment has the correct author.
		} catch (CommentException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * This test verifies get comments raises an exception when called with a
	 * non-existent user.
	 */
	@Test
	public void testGetMissingUserComments() {
		CommentException thrown = assertThrows(CommentException.class, () -> commentService.getCommentsByUser(0));
		assertTrue(thrown.getMessage().contains("No such user."));
	}

	/**
	 * Test the DTO conversion.
	 */
	@Test
	public void testCommentToDTO() {
		try {
			// Create comment and user.
			User user = new User();
			user.setId(USER_ID);
			user.setUserName(USER_NAME);
			user.setEmail(USER_EMAIL);
			user.setPassword(USER_PASSWORD);
			Comment comment = new Comment();
			comment.setUser(user);
			comment.setText(COMMENT_TEXT);
			comment.setId(COMMENT_ID);
			comment.setDatePosted(COMMENT_DATE);
			comment.setTime(COMMENT_TIME);

			// Convert.
			CommentDTO commentDTO = CommentService.commentToDto(comment);

			// Test values.
			assertEquals(COMMENT_ID, commentDTO.getId());
			assertEquals(USER_NAME, commentDTO.getUsername());
			assertEquals(COMMENT_TEXT, commentDTO.getText());
			assertEquals(COMMENT_DATE, commentDTO.getDatePosted());
			assertEquals(COMMENT_TIME, commentDTO.getTime());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
