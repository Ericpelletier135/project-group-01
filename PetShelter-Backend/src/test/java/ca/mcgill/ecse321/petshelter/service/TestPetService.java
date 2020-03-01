package ca.mcgill.ecse321.petshelter.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ca.mcgill.ecse321.petshelter.dto.PetDTO;
import ca.mcgill.ecse321.petshelter.model.Gender;
import ca.mcgill.ecse321.petshelter.model.Pet;
import ca.mcgill.ecse321.petshelter.model.User;
import ca.mcgill.ecse321.petshelter.model.UserType;
import ca.mcgill.ecse321.petshelter.repository.PetRepository;
import ca.mcgill.ecse321.petshelter.repository.UserRepository;
import ca.mcgill.ecse321.petshelter.service.exception.PetException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.*;

//TODO Javadoc
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("deprecation")
public class TestPetService {

    @Mock
    private PetRepository petDao;

    @Mock
    private UserRepository userDao;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private PetService petService;

    //User1 parameters
    private static final String USER_NAME1 = "testUN";
    private static final String USER_EMAIL1 = "username@gmail.com";
    private static final String USER_PASSWORD1 = "password1";
    private static final UserType USER_TYPE1 = UserType.USER;

    //User2 parameters
    private static final String USER_NAME2 = "secondUser";
    private static final String USER_EMAIL2 = "username2@gmail.com";
    private static final String USER_PASSWORD2 = "password2";
    private static final UserType USER_TYPE2 = UserType.USER;

    
    
    private static final Set<Pet> USER_PETS = new HashSet<>();
    
    //User parameters
    private static final String USER_NAME = "testUN";
    private static final String USER_EMAIL = "username@gmail.com";
    private static final String USER_PASSWORD = "testUN";
    private static final UserType USER_TYPE = UserType.USER;
    
    
    //Pet parameters
    private static final Date PET_DOB = new Date(119, 10, 20);
    private static final String PET_NAME = "testPettt";
    private static final String PET_SPECIES = "testSpecies";
    private static final String PET_BREED = "testBreed";
    private static final String PET_DESCRIPTION = "testDesc";
    private static final Set<Pet> USER1_PETS = new HashSet<Pet>();
    private static final Set<Pet> USER2_PETS = new HashSet<Pet>();
    private static final Gender PET_GENDER = Gender.FEMALE;
    private static final byte[] PET_PICTURE = new byte[5];
    //Updated pet parameter
    private static final Date PET_DOB_UPDATE = new Date(11, 2, 3);
    private static final String PET_NAME_UPDATE = "newPetName";
    private static final String PET_SPECIES_UPDATE = "newTestSpecies";
    private static final String PET_BREED_UPDATE = "newTestBreed";
    private static final String PET_DESCRIPTION_UPDATE = "newTestDesc";
    private static final Gender PET_GENDER_UPDATE = Gender.MALE;
    private static final byte[] PET_PICTURE_UPDATE = new byte[10];
    private long PET_ID = 0;
    private long petId;
    private Pet pet;
    private User user1;
    private User user2;
    private PetDTO petDto;

    //TODO this is not called
    @AfterEach
    @BeforeEach
    public void clearDatabase() {
        userDao.deleteAll();
        petDao.deleteAll();
    }

    @Mock
    private PetRepository petRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @BeforeEach
    public void setMockOutput() {
        MockitoAnnotations.initMocks(this);
        lenient().when(userRepository.findUserByUserName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArgument(0).equals(USER_NAME)) {
                User user = new User();
                user.setUserName(USER_NAME);
                user.setEmail(USER_EMAIL);
                user.setPassword(USER_PASSWORD);
                user.setPets(USER_PETS);
                return user;
            } else {
                return null;
            }
        });

        //this will never work because petid is null.....
        lenient().when(petRepository.findPetById(anyLong())).thenAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArgument(0).equals(PET_ID)) {
                Pet pet = new Pet();
                pet.setBreed(PET_BREED);
                pet.setDateOfBirth(PET_DOB);
                pet.setDescription(PET_DESCRIPTION);
                pet.setName(PET_NAME);
                pet.setSpecies(PET_SPECIES);
                pet.setPicture(PET_PICTURE);
                return pet;
            } else {
                return null;
            }
        });
        
        lenient().when(userRepository.findUserByPets(any())).thenAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArgument(0) instanceof Pet) {
                User user = new User();
                user.setUserName(USER_NAME);
                user.setEmail(USER_EMAIL);
                user.setPassword(USER_PASSWORD);
                user.setPets(USER_PETS);
                return user;
            } else {
                return null;
            }
        });


        Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
            return invocation.getArgument(0);
        };
        lenient().when(userDao.save(any(User.class))).thenAnswer(returnParameterAsAnswer);
        lenient().when(petDao.save(any(Pet.class))).thenAnswer(returnParameterAsAnswer);

    }

    //TODO are we doing this?
    /**
     * Creates a test user in the database.
     * @author Katrina
     * @return user created
     */
    public User createUser() {
        User user = new User();
        String userName = "testUN";
        String password = "myPassword1!";
        String email = "TestUserName@gmail.com";
        UserType userType = UserType.USER;

        user.setUserName(userName);
        user.setUserType(userType);
        user.setPassword(password);
        user.setEmail(email);
        userDao.save(user);

        return user;
    }


    ////////////////////////////// CREATE PET //////////////////////////////

    /**
     * Normal test case. Creates a pet. Should not throw any exception".
        
        Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> invocation.getArgument(0);
        lenient().when(userRepository.save(any(User.class))).thenAnswer(returnParameterAsAnswer);
        lenient().when(petRepository.save(any(Pet.class))).thenAnswer(returnParameterAsAnswer);
*/
    @Test
    public void testCreatePet() {

        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
        
        PetDTO pet = null;
        
        try {
            pet = petService.createPet(petDTO);
        } catch (PetException e) {
            e.printStackTrace();
        }
        
        assertNotNull(pet);
        assertEquals(PET_DOB, pet.getDateOfBirth());
        assertEquals(PET_NAME, pet.getName());
    }
    
    @Test
    public void testCreatePetNoUser() {
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        
        try {
            petService.createPet(petDTO);
        } catch (PetException e) {
            assertEquals("Cannot add: User does not exist.", e.getMessage());
        }
    }
    
    @Test
    public void testCreatePetNoName() {
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
        
        try {
            petService.createPet(petDTO);
        } catch (PetException e) {
            assertEquals("Cannot add: A pet needs a name.", e.getMessage());
        }
    }
    
    @Test
    public void testCreatePetNoSpecies() {
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setUserName(USER_NAME);
        
        try {
            petService.createPet(petDTO);
        } catch (PetException e) {
            assertEquals("Cannot add: A pet needs a species.", e.getMessage());
        }
    }
    
    @Test
    public void testCreatePetNoBreed() {
        PetDTO petDTO = new PetDTO();
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
        
        try {
            petService.createPet(petDTO);
        } catch (PetException e) {
            assertEquals("Cannot add: A pet needs a breed.", e.getMessage());
        }
    }
    
    @Test
    public void testCreatePetNoDesc() {
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO pet = null;
        try {
            pet = petService.createPet(petDTO);
        } catch (PetException e) {
            e.printStackTrace();
        }
        assertNotNull(pet);
        assertEquals("", pet.getDescription());
    }
    
    @Test
    public void testCreatePetNoGender() {
        
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
        
        try {
            petService.createPet(petDTO);
        } catch (PetException e) {
            assertEquals("Cannot add: A pet needs a gender.", e.getMessage());
        }
    }
    
    @Test
    public void testEditPet() {
        //creates pet
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO oldPet = petService.createPet(petDTO);
        
        assertEquals(PET_NAME, oldPet.getName());
        
        //update the pet
        PetDTO petUpdateDTO = new PetDTO();
        petUpdateDTO.setBreed(PET_BREED_UPDATE);
        petUpdateDTO.setDateOfBirth(PET_DOB_UPDATE);
        petUpdateDTO.setDescription(PET_DESCRIPTION_UPDATE);
        petUpdateDTO.setGender(PET_GENDER_UPDATE);
        petUpdateDTO.setName(PET_NAME_UPDATE);
        petUpdateDTO.setPicture(PET_PICTURE_UPDATE);
        petUpdateDTO.setSpecies(PET_SPECIES_UPDATE);
        petUpdateDTO.setUserName(USER_NAME);
        petUpdateDTO.setId(oldPet.getId());
    
        PetDTO pet = petService.editPet(petUpdateDTO);
        
        assertNotNull(pet);
        assertEquals(PET_NAME_UPDATE, pet.getName());
        assertEquals(PET_BREED_UPDATE, pet.getBreed());
    }
    
    @Test
    public void testEditPetNoSpecies() {
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO oldPet = petService.createPet(petDTO);
        
        assertEquals(PET_NAME, oldPet.getName());
        
        //update the pet
        PetDTO petUpdateDTO = new PetDTO();
        petUpdateDTO.setBreed(PET_BREED_UPDATE);
        petUpdateDTO.setDateOfBirth(PET_DOB_UPDATE);
        petUpdateDTO.setDescription(PET_DESCRIPTION_UPDATE);
        petUpdateDTO.setGender(PET_GENDER_UPDATE);
        petUpdateDTO.setName(PET_NAME_UPDATE);
        petUpdateDTO.setPicture(PET_PICTURE_UPDATE);
        // petUpdateDTO.setSpecies(PET_SPECIES_UPDATE);
        petUpdateDTO.setUserName(USER_NAME);
        petUpdateDTO.setId(oldPet.getId());
        
        try {
            petService.editPet(petUpdateDTO);
        } catch (PetException e) {
            assertEquals("Cannot edit: A pet needs a species.", e.getMessage());
        }
    }
    
    @Test
    public void testEditPetNoBreed() {
        
        //creates pet
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO oldPet = petService.createPet(petDTO);
        
        assertEquals(PET_NAME, oldPet.getName());
        
        //update the pet
        PetDTO petUpdateDTO = new PetDTO();
        //     petUpdateDTO.setBreed(PET_BREED_UPDATE);
        petUpdateDTO.setDateOfBirth(PET_DOB_UPDATE);
        petUpdateDTO.setDescription(PET_DESCRIPTION_UPDATE);
        petUpdateDTO.setGender(PET_GENDER_UPDATE);
        petUpdateDTO.setName(PET_NAME_UPDATE);
        petUpdateDTO.setPicture(PET_PICTURE_UPDATE);
        petUpdateDTO.setSpecies(PET_SPECIES_UPDATE);
        petUpdateDTO.setUserName(USER_NAME);
        petUpdateDTO.setId(oldPet.getId());
        
        try {
            petService.editPet(petUpdateDTO);
        } catch (PetException e) {
            assertEquals("Cannot edit: A pet needs a breed.", e.getMessage());
        }
    }
    
    @Test
    public void testEditPetNoDesc() {
        
        //creates pet
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO oldPet = petService.createPet(petDTO);
        
        assertEquals(PET_NAME, oldPet.getName());
        
        //update the pet
        PetDTO petUpdateDTO = new PetDTO();
        petUpdateDTO.setBreed(PET_BREED_UPDATE);
        petUpdateDTO.setDateOfBirth(PET_DOB_UPDATE);
        //   petUpdateDTO.setDescription(PET_DESCRIPTION_UPDATE);
        petUpdateDTO.setGender(PET_GENDER_UPDATE);
        petUpdateDTO.setName(PET_NAME_UPDATE);
        petUpdateDTO.setPicture(PET_PICTURE_UPDATE);
        petUpdateDTO.setSpecies(PET_SPECIES_UPDATE);
        petUpdateDTO.setUserName(USER_NAME);
        petUpdateDTO.setId(oldPet.getId());
        
        try {
            petService.editPet(petUpdateDTO);
        } catch (PetException e) {
            assertEquals("", e.getMessage());
        }
    }
    
    @Test
    public void testEditPetNoGender() {
        
        //creates pet
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO oldPet = petService.createPet(petDTO);
        
        assertEquals(PET_NAME, oldPet.getName());
        
        //update the pet
        PetDTO petUpdateDTO = new PetDTO();
        petUpdateDTO.setBreed(PET_BREED_UPDATE);
        petUpdateDTO.setDateOfBirth(PET_DOB_UPDATE);
        petUpdateDTO.setDescription(PET_DESCRIPTION_UPDATE);
        //  petUpdateDTO.setGender(PET_GENDER_UPDATE);
        petUpdateDTO.setName(PET_NAME_UPDATE);
        petUpdateDTO.setPicture(PET_PICTURE_UPDATE);
        petUpdateDTO.setSpecies(PET_SPECIES_UPDATE);
        petUpdateDTO.setUserName(USER_NAME);
        petUpdateDTO.setId(oldPet.getId());
    
        try {
            petService.editPet(petUpdateDTO);
        } catch (PetException e) {
            assertEquals("Cannot edit: A pet needs a gender.", e.getMessage());
        }
    }
    
    @Test
    public void testEditPetNoNewOwner() {
        
        //creates pet
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO oldPet = petService.createPet(petDTO);
        
        assertEquals(PET_NAME, oldPet.getName());
        
        //update the pet
        PetDTO petUpdateDTO = new PetDTO();
        petUpdateDTO.setBreed(PET_BREED_UPDATE);
        petUpdateDTO.setDateOfBirth(PET_DOB_UPDATE);
        petUpdateDTO.setDescription(PET_DESCRIPTION_UPDATE);
        petUpdateDTO.setGender(PET_GENDER_UPDATE);
        petUpdateDTO.setName(PET_NAME_UPDATE);
        petUpdateDTO.setPicture(PET_PICTURE_UPDATE);
        petUpdateDTO.setSpecies(PET_SPECIES_UPDATE);
        //  petUpdateDTO.setUserName(USER_NAME);
        petUpdateDTO.setId(oldPet.getId());
        
        try {
            petService.editPet(petUpdateDTO);
        } catch (PetException e) {
            assertEquals("Cannot edit: User not found.", e.getMessage());
        }
    }
    
    @Test
    public void testEditPetNoNewName() {
        
        //creates pet
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO oldPet = petService.createPet(petDTO);
        
        assertEquals(PET_NAME, oldPet.getName());
        
        //update the pet
        PetDTO petUpdateDTO = new PetDTO();
        petUpdateDTO.setBreed(PET_BREED_UPDATE);
        petUpdateDTO.setDateOfBirth(PET_DOB_UPDATE);
        petUpdateDTO.setDescription(PET_DESCRIPTION_UPDATE);
        petUpdateDTO.setGender(PET_GENDER_UPDATE);
        // petUpdateDTO.setName(PET_NAME_UPDATE);
        petUpdateDTO.setPicture(PET_PICTURE_UPDATE);
        petUpdateDTO.setSpecies(PET_SPECIES_UPDATE);
        petUpdateDTO.setUserName(USER_NAME);
        petUpdateDTO.setId(oldPet.getId());
        
        try {
            petService.editPet(petUpdateDTO);
        } catch (PetException e) {
            assertEquals("Cannot edit: A pet needs a name.", e.getMessage());
        }
    }
    
    @Test
    public void testEditPetWrongPet() {
        
        //creates pet
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO oldPet = petService.createPet(petDTO);
        
        assertEquals(PET_NAME, oldPet.getName());
        
        //update the pet
        PetDTO petUpdateDTO = new PetDTO();
        petUpdateDTO.setBreed(PET_BREED_UPDATE);
        petUpdateDTO.setDateOfBirth(PET_DOB_UPDATE);
        petUpdateDTO.setDescription(PET_DESCRIPTION_UPDATE);
        petUpdateDTO.setGender(PET_GENDER_UPDATE);
        petUpdateDTO.setName(PET_NAME_UPDATE);
        petUpdateDTO.setPicture(PET_PICTURE_UPDATE);
        petUpdateDTO.setSpecies(PET_SPECIES_UPDATE);
        petUpdateDTO.setUserName(USER_NAME);
        //   petUpdateDTO.setId(oldPet.getId());
        
        try {
            petService.editPet(petUpdateDTO);
        } catch (PetException e) {
            assertEquals("Cannot edit: Pet does not exist.", e.getMessage());
        }
    }
    
    
    @Test
    public void deletePet() {
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO pet = null;
        
        try {
            pet = petService.createPet(petDTO);
        } catch (PetException e) {
            e.printStackTrace();
        }
        assertNotNull(pet);
        
        petDTO.setId(pet.getId());
        
        boolean isDeleted = false;
        try {
            isDeleted = petService.deletePet(petDTO.getId(), USER_NAME);
        } catch (PetException e) {
            e.printStackTrace();
        }
        assertTrue(isDeleted);
    }
    
    @Test
    public void deletePetFail() {
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
        try {
            petService.deletePet(petDTO.getId(), USER_NAME);
        } catch (PetException e) {
            assertEquals("Cannot delete: Pet does not exist.", e.getMessage());
        }
    }
    
    @Test
    public void findPet() {
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        PetDTO pet = null;
        
        try {
            pet = petService.createPet(petDTO);
        } catch (PetException e) {
            e.printStackTrace();
        }
        
        assertNotNull(pet);
        petDTO.setId(pet.getId());
    
        PetDTO dbPet = petService.getPet(petDTO);
        assertEquals(pet.getName(), dbPet.getName());
        assertEquals(pet.getBreed(), dbPet.getBreed());
    }
    
    
    @Test
    public void findPetNoID() {
        PetDTO petDTO = new PetDTO();
        petDTO.setBreed(PET_BREED);
        petDTO.setDateOfBirth(PET_DOB);
        petDTO.setDescription(PET_DESCRIPTION);
        petDTO.setGender(PET_GENDER);
        petDTO.setName(PET_NAME);
        petDTO.setPicture(PET_PICTURE);
        petDTO.setSpecies(PET_SPECIES);
        petDTO.setUserName(USER_NAME);
    
        try {
            petService.getPet(petDTO);
        } catch (PetException e) {
            assertEquals("Pet does not exist.", e.getMessage());
        }
    }
    
    @Test
    public void getPetsByWrongUserName() {
        try {
            petService.getPetsByUser("bob");
        } catch (PetException e) {
            assertEquals("User does not exist.", e.getMessage());
        }
    }
}
