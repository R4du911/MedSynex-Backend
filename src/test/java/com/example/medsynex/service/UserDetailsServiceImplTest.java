package com.example.medsynex.service;

import com.example.medsynex.controller.AuthController;
import com.example.medsynex.dto.register.RegisterRequestDTO;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.*;
import com.example.medsynex.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private FamilyDoctorRepository familyDoctorRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private FamilyDoctorRequestService familyDoctorRequestService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private final String key = "1234567890123456";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userDetailsService, "key", key);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(username));

        assertEquals("User Not Found with username: " + username, exception.getMessage());
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void testGetCurrentUserDetails_UserNotFound() {
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.getCurrentUserDetails(username));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testGetCurrentUserDetails_UserFound() throws BusinessException {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User foundUser = userDetailsService.getCurrentUserDetails(username);

        assertEquals(username, foundUser.getUsername());
    }

    @Test
    void testGetAllUsersWhichAreRegisteredAsFamilyDoctors() {
        User user1 = new User();
        user1.setUsername("doctor1");

        User user2 = new User();
        user2.setUsername("doctor2");

        when(userRepository.findAllByFamilyDoctorIsNotNull()).thenReturn(List.of(user1, user2));

        List<User> doctors = userDetailsService.getAllUsersWhichAreRegisteredAsFamilyDoctors();

        assertEquals(2, doctors.size());
        assertEquals("doctor1", doctors.get(0).getUsername());
        assertEquals("doctor2", doctors.get(1).getUsername());
    }

    @Test
    void testGetAllUsersWhichAreRegisteredAsPatients() {
        User user1 = new User();
        user1.setUsername("patient1");
        User user2 = new User();
        user2.setUsername("patient2");

        when(userRepository.findAllByPatientIsNotNull()).thenReturn(List.of(user1, user2));

        List<User> patients = userDetailsService.getAllUsersWhichAreRegisteredAsPatients();

        assertEquals(2, patients.size());
        assertEquals("patient1", patients.get(0).getUsername());
        assertEquals("patient2", patients.get(1).getUsername());
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("Radu", "Vatican", "testUser",
                "test@example.com", "6Rp5ocU58EMJaVzsUSowuw==", ERole.PAT);

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(new User()));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUser(registerRequest));

        assertEquals(BusinessExceptionCode.USERNAME_ALREADY_REGISTERED, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("Radu", "Vatican", "testUser",
                "test@example.com", "6Rp5ocU58EMJaVzsUSowuw==", ERole.PAT);

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(new User()));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUser(registerRequest));

        assertEquals(BusinessExceptionCode.EMAIL_ALREADY_REGISTERED, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUser_InvalidFirstName() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("", "Vatican", "testUser",
                "test@example.com", "6Rp5ocU58EMJaVzsUSowuw==", ERole.PAT);

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUser(registerRequest));

        assertEquals(BusinessExceptionCode.INVALID_USER_FORMAT, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUser_InvalidEmail() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("Radu", "Vatican", "testUser",
                "invalid", "6Rp5ocU58EMJaVzsUSowuw==", ERole.PAT);

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUser(registerRequest));

        assertEquals(BusinessExceptionCode.INVALID_USER_FORMAT, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUser_InvalidPassword() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("Radu", "Vatican", "testUser",
                "test@example.com", "6Rp5ocU58EMJaVzsUSowuw==", ERole.PAT);

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

        try (MockedStatic<AuthController> mockedAuthController = mockStatic(AuthController.class)) {
            String decryptedPassword = "invalidPassword";
            mockedAuthController.when(() -> AuthController.decrypt(registerRequest.getPassword(), key)).thenReturn(decryptedPassword);

            BusinessException exception = assertThrows(BusinessException.class, () ->
                    userDetailsService.registerUser(registerRequest));

            assertEquals(BusinessExceptionCode.INVALID_USER_FORMAT, exception.getBusinessExceptionCode());
        }
    }

    @Test
    void testRegisterUser_RoleNotFound() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("Radu", "Vatican", "testUser",
                "test@example.com", "6Rp5ocU58EMJaVzsUSowuw==", ERole.PAT);

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName(registerRequest.getRole())).thenReturn(Optional.empty());

        try (MockedStatic<AuthController> mockedAuthController = mockStatic(AuthController.class)) {
            String decryptedPassword = "validPassword100R$";
            mockedAuthController.when(() -> AuthController.decrypt(registerRequest.getPassword(), key)).thenReturn(decryptedPassword);

            BusinessException exception = assertThrows(BusinessException.class, () ->
                    userDetailsService.registerUser(registerRequest));

            assertEquals(BusinessExceptionCode.INVALID_USER_FORMAT, exception.getBusinessExceptionCode());
        }
    }

    @Test
    void testRegisterUser_Success() throws BusinessException {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("Radu", "Vatican", "testUser",
                "test@example.com", "6Rp5ocU58EMJaVzsUSowuw==", ERole.PAT);

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

        try (MockedStatic<AuthController> mockedAuthController = mockStatic(AuthController.class)) {
            mockedAuthController.when(() -> AuthController.decrypt(registerRequest.getPassword(), key)).thenReturn("validPassword100$R");

            when(roleRepository.findByName(registerRequest.getRole())).thenReturn(Optional.of(new Role()));

            userDetailsService.registerUser(registerRequest);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertEquals(registerRequest.getFirstName(), savedUser.getFirstName());
            assertEquals(registerRequest.getLastName(), savedUser.getLastName());
            assertEquals(registerRequest.getUsername(), savedUser.getUsername());
            assertEquals(registerRequest.getEmail(), savedUser.getEmail());
            assert(savedUser.isFirstLogin());
        }
    }

    @Test
    void testRegisterUserAsPatient_UserNotFound() {
        String username = "testUser";
        Long cnp = 1234567890123L;
        FamilyDoctor familyDoctor = new FamilyDoctor();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUserAsPatient(username, cnp, familyDoctor));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUserAsPatient_PatientAlreadyRegistered() {
        String username = "testUser";
        Long cnp = 1234567890123L;
        FamilyDoctor familyDoctor = new FamilyDoctor();
        User user = new User();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(new Patient()));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUserAsPatient(username, cnp, familyDoctor));

        assertEquals(BusinessExceptionCode.PATIENT_ALREADY_REGISTERED, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUserAsPatient_FamilyDoctorHasMaxPatients() {
        String username = "testUser";
        Long cnp = 1234567890123L;
        FamilyDoctor familyDoctor = new FamilyDoctor();
        familyDoctor.setNrPatients(20);
        User user = new User();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(patientRepository.findById(cnp)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUserAsPatient(username, cnp, familyDoctor));

        assertEquals(BusinessExceptionCode.FAMILY_DOCTOR_HAS_MAX_PATIENTS, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUserAsPatient_Success() throws BusinessException {
        String username = "testUser";
        Long cnp = 1234567890123L;
        FamilyDoctor familyDoctor = new FamilyDoctor();
        familyDoctor.setNrPatients(10);
        User user = new User();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(patientRepository.findById(cnp)).thenReturn(Optional.empty());

        Patient savedPatient = Patient.builder().cnp(cnp).build();
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        userDetailsService.registerUserAsPatient(username, cnp, familyDoctor);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertFalse(updatedUser.isFirstLogin());
        assertEquals(savedPatient, updatedUser.getPatient());
        assertNull(updatedUser.getFamilyDoctor());
        assertNull(updatedUser.getDoctor());
        assertNull(updatedUser.getLaboratory());

        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(patientCaptor.capture());
        Patient savedPatientToDb = patientCaptor.getValue();

        assertEquals(cnp, savedPatientToDb.getCnp());
        assertNull(savedPatientToDb.getFamilyDoctor());

        verify(familyDoctorRequestService).makeAFamilyDoctorRequest(username, familyDoctor);
    }

    @Test
    void testRegisterUserAsFamilyDoctor_UserNotFound() {
        String username = "testUser";
        Dispensary dispensary = new Dispensary();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUserAsFamilyDoctor(username, dispensary));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUserAsFamilyDoctor_Success() throws BusinessException {
        String username = "testUser";
        Dispensary dispensary = new Dispensary();
        User user = new User();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        FamilyDoctor savedFamilyDoctor = FamilyDoctor.builder().dispensary(dispensary).nrPatients(0).build();
        when(familyDoctorRepository.save(any(FamilyDoctor.class))).thenReturn(savedFamilyDoctor);

        userDetailsService.registerUserAsFamilyDoctor(username, dispensary);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertFalse(updatedUser.isFirstLogin());
        assertNull(updatedUser.getPatient());
        assertEquals(savedFamilyDoctor, updatedUser.getFamilyDoctor());
        assertNull(updatedUser.getDoctor());
        assertNull(updatedUser.getLaboratory());

        ArgumentCaptor<FamilyDoctor> familyDoctorCaptor = ArgumentCaptor.forClass(FamilyDoctor.class);
        verify(familyDoctorRepository).save(familyDoctorCaptor.capture());
        FamilyDoctor savedFamilyDoctorToDb = familyDoctorCaptor.getValue();

        assertEquals(dispensary, savedFamilyDoctorToDb.getDispensary());
        assertEquals(0, savedFamilyDoctorToDb.getNrPatients());
    }

    @Test
    void testRegisterUserAsDoctor_UserNotFound() {
        String username = "testUser";
        Hospital hospital = new Hospital();
        Specialization specialization = new Specialization();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUserAsDoctor(username, hospital, specialization));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUserAsDoctor_Success() throws BusinessException {
        String username = "testUser";
        Hospital hospital = new Hospital();
        Specialization specialization = new Specialization();
        User user = new User();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Doctor savedDoctor = Doctor.builder().hospital(hospital).specialization(specialization).build();
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        userDetailsService.registerUserAsDoctor(username, hospital, specialization);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertFalse(updatedUser.isFirstLogin());
        assertNull(updatedUser.getPatient());
        assertNull(updatedUser.getFamilyDoctor());
        assertEquals(savedDoctor, updatedUser.getDoctor());
        assertNull(updatedUser.getLaboratory());

        ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(doctorCaptor.capture());
        Doctor savedDoctorToDb = doctorCaptor.getValue();

        assertEquals(hospital, savedDoctorToDb.getHospital());
        assertEquals(specialization, savedDoctorToDb.getSpecialization());
    }

    @Test
    void testRegisterUserAsLaboratory_UserNotFound() {
        String username = "testUser";
        Laboratory laboratory = new Laboratory();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userDetailsService.registerUserAsLaboratory(username, laboratory));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testRegisterUserAsLaboratory_Success() throws BusinessException {
        String username = "testUser";
        Laboratory laboratory = new Laboratory();
        User user = new User();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        userDetailsService.registerUserAsLaboratory(username, laboratory);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertFalse(updatedUser.isFirstLogin());
        assertNull(updatedUser.getPatient());
        assertNull(updatedUser.getFamilyDoctor());
        assertNull(updatedUser.getDoctor());
        assertEquals(laboratory, updatedUser.getLaboratory());
    }

    @Test
    void validateNames() {
        //ECP
        assert(userDetailsService.validateNames("Radu")); // Valid names that match the pattern
        assert(!userDetailsService.validateNames("radu")); // No uppercase letter (invalid)
        assert(!userDetailsService.validateNames("Radu3")); // Characters other than letters (invalid)
        assert(!userDetailsService.validateNames("")); // Empty string (invalid)
        assert(!userDetailsService.validateNames(null)); // Null (invalid)

        //BVA
        assert(!userDetailsService.validateNames("I")); // Just one uppercase letter, no lowercase following (invalid)
        assert(userDetailsService.validateNames("Io")); // One uppercase followed by one lowercase, minimal valid case
        assert(userDetailsService.validateNames("Ion")); // One uppercase followed by two lowercase (valid)
    }

    @Test
    void validatePassword() {
        //ECP
        assert(userDetailsService.validatePassword("Valid123$")); // Valid passwords that meet all criteria
        assert(!userDetailsService.validatePassword("Va1%")); // Shorter than 8 characters (invalid)
        assert(!userDetailsService.validatePassword("INVALID123$")); // Missing lowercase letter (invalid)
        assert(!userDetailsService.validatePassword("invalid123$")); // Missing uppercase letter (invalid)
        assert(!userDetailsService.validatePassword("Invalid$abc")); // Missing at least one digit (invalid)
        assert(!userDetailsService.validatePassword("Invalid123")); // Missing special character (invalid)
        assert(!userDetailsService.validatePassword("")); // Empty string (invalid)
        assert(!userDetailsService.validatePassword(null)); // Null (invalid)

        //BVA
        assert(!userDetailsService.validatePassword("Val123$")); // 7 characters, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("Val123$D")); // 8 characters, the minimum valid length (valid)
        assert(userDetailsService.validatePassword("Val123$D!")); // 9 characters, just above the minimum (valid)

        assert(!userDetailsService.validatePassword("ABIDE123!")); // No lowercase letter, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("AbCDE123!")); // 1 lowercase letter, minimal valid case
        assert(userDetailsService.validatePassword("AbcDE123!")); // 2 lowercase letters, just above the minimum (valid)

        assert(!userDetailsService.validatePassword("abide123!")); // No uppercase letter, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("aBide123!")); // 1 uppercase letter, minimal valid case
        assert(userDetailsService.validatePassword("aBIde123!")); // 2 uppercase letters, just above the minimum (valid)

        assert(!userDetailsService.validatePassword("Bonus$%&")); // No digit, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("Bonus$%&1")); // 1 digit, minimal valid case
        assert(userDetailsService.validatePassword("Bonus$%&12")); // 2 digits, just above the minimum (valid)

        assert(!userDetailsService.validatePassword("Bonus1234")); // No special character, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("Bonus1234!")); // 1 special character, minimal valid case
        assert(userDetailsService.validatePassword("Bonus1234!@")); // 2 special characters, just above the minimum (valid)
    }

    @Test
    void validateEmail() {
        assert(userDetailsService.validateEmail("valid_mail@yahoo.com"));
        assert(!userDetailsService.validateEmail("invalid"));
        assert(!userDetailsService.validateEmail("invalid@"));
        assert(!userDetailsService.validateEmail("invalid@yahoo"));
        assert(!userDetailsService.validateEmail("invalid@yahoo."));
        assert(!userDetailsService.validateEmail(""));
        assert(!userDetailsService.validateEmail(null));
    }
}
