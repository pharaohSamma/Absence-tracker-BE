package com.example.studentabsencetracker.service;

import com.example.studentabsencetracker.exception.BadRequestException;
import com.example.studentabsencetracker.exception.ResourceNotFoundException;
import com.example.studentabsencetracker.model.dto.request.ClassRequest;
import com.example.studentabsencetracker.model.dto.response.ClassResponse;
import com.example.studentabsencetracker.model.entity.CourseClass;
import com.example.studentabsencetracker.model.entity.User;
import com.example.studentabsencetracker.model.enums.RoleType;
import com.example.studentabsencetracker.repository.ClassRepository;
import com.example.studentabsencetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ClassResponse> getAllClasses() {
        return classRepository.findAll().stream()
                .map(ClassResponse::new)
                .collect(Collectors.toList());
    }

    public ClassResponse getClassById(Long id) {
        CourseClass courseClassEntity = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));
        return new ClassResponse(courseClassEntity);
    }

    public ClassResponse getClassByCode(String classCode) {
        CourseClass courseClassEntity = classRepository.findByClassCode(classCode)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with code: " + classCode));
        return new ClassResponse(courseClassEntity);
    }

    public List<ClassResponse> getClassesByTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        if (teacher.getRole() != RoleType.TEACHER) {
            throw new BadRequestException("User is not a teacher");
        }

        return classRepository.findByTeacher(teacher).stream()
                .map(ClassResponse::new)
                .collect(Collectors.toList());
    }

    public List<ClassResponse> getClassesByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (student.getRole() != RoleType.STUDENT) {
            throw new BadRequestException("User is not a student");
        }

        return classRepository.findByStudentsContaining(student).stream()
                .map(ClassResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassResponse createClass(ClassRequest classRequest) {
        // Check if class code already exists
        if (classRepository.existsByClassCode(classRequest.getClassCode())) {
            throw new BadRequestException("Class code already exists");
        }

        CourseClass courseClassEntity = new CourseClass();
        courseClassEntity.setClassCode(classRequest.getClassCode());
        courseClassEntity.setName(classRequest.getName());
        courseClassEntity.setDescription(classRequest.getDescription());

        // Assign teacher if provided
        if (classRequest.getTeacherId() != null) {
            User teacher = userRepository.findById(classRequest.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + classRequest.getTeacherId()));

            if (teacher.getRole() != RoleType.TEACHER) {
                throw new BadRequestException("User is not a teacher");
            }

            courseClassEntity.setTeacher(teacher);
        }

        classRepository.save(courseClassEntity);

        return new ClassResponse(courseClassEntity);
    }

    @Transactional
    public ClassResponse updateClass(Long id, ClassRequest classRequest) {
        CourseClass courseClassEntity = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));

        // Check if class code is being changed and if it already exists
        if (!courseClassEntity.getClassCode().equals(classRequest.getClassCode()) &&
                classRepository.existsByClassCode(classRequest.getClassCode())) {
            throw new BadRequestException("Class code already exists");
        }

        courseClassEntity.setClassCode(classRequest.getClassCode());
        courseClassEntity.setName(classRequest.getName());
        courseClassEntity.setDescription(classRequest.getDescription());

        // Update teacher if provided
        if (classRequest.getTeacherId() != null) {
            User teacher = userRepository.findById(classRequest.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + classRequest.getTeacherId()));

            if (teacher.getRole() != RoleType.TEACHER) {
                throw new BadRequestException("User is not a teacher");
            }

            courseClassEntity.setTeacher(teacher);
        } else {
            courseClassEntity.setTeacher(null);
        }

        classRepository.save(courseClassEntity);

        return new ClassResponse(courseClassEntity);
    }

    @Transactional
    public ClassResponse assignTeacher(Long classId, Long teacherId) {
        CourseClass courseClassEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        if (teacher.getRole() != RoleType.TEACHER) {
            throw new BadRequestException("User is not a teacher");
        }

        courseClassEntity.setTeacher(teacher);
        classRepository.save(courseClassEntity);

        return new ClassResponse(courseClassEntity);
    }

    @Transactional
    public ClassResponse enrollStudent(Long classId, Long studentId) {
        CourseClass courseClassEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (student.getRole() != RoleType.STUDENT) {
            throw new BadRequestException("User is not a student");
        }

        courseClassEntity.getStudents().add(student);
        classRepository.save(courseClassEntity);

        return new ClassResponse(courseClassEntity);
    }

    @Transactional
    public ClassResponse removeStudent(Long classId, Long studentId) {
        CourseClass courseClassEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        courseClassEntity.removeStudent(student);
        classRepository.save(courseClassEntity);

        return new ClassResponse(courseClassEntity);
    }

    public void deleteClass(Long id) {
        if (!classRepository.existsById(id)) {
            throw new ResourceNotFoundException("Class not found with id: " + id);
        }
        classRepository.deleteById(id);
    }
}