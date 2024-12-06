package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.repo.CourseRepo;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CourseRepo courseRepo;

    // Get all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // Get course by ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        Course course = courseService.getCourseById(id);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    // Get course by name
    @GetMapping("/name/{courseName}")
    public ResponseEntity<Course> getCourseByName(@PathVariable String courseName) {
        Course course = courseService.getCourseByName(courseName);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    // Add a new course
    @PostMapping
    public ResponseEntity<Course> addCourse(  @RequestParam("courseName") String courseName,
                                              @RequestParam("courseCode") String courseCode,
                                              @RequestParam("description") String description,
                                              @RequestParam("teacherId") String teacherId,
                                              @RequestParam("startingDate") String startingDate,
                                              @RequestParam("endDate") String endDate,
                                              @RequestParam("image") MultipartFile image) throws IOException {
        String imageUrl = cloudinaryService.uploadFile(image);
        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setDescription(description);
        course.setCourseName(courseName);
        course.setStartingDate(startingDate);
        course.setEndDate(endDate);
        course.setImage(imageUrl);
        course.setTeacherId(teacherId);
        Course createdCourse = courseService.addCourse(course);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable String id,
                                               @RequestParam("courseName") String courseName,
                                               @RequestParam("courseCode") String courseCode,
                                               @RequestParam("description") String description,
                                               @RequestParam("teacherId") String teacherId,
                                               @RequestParam("startingDate") String startingDate,
                                               @RequestParam("endDate") String endDate,
                                               @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        System.out.println(image);
        // Fetch the existing course details from the database
        Course existingCourse = courseService.getCourseById(id);
        if (existingCourse == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if course doesn't exist
        }

        // Update fields from the request
        existingCourse.setCourseCode(courseCode);
        existingCourse.setDescription(description);
        existingCourse.setCourseName(courseName);
        existingCourse.setStartingDate(startingDate);
        existingCourse.setEndDate(endDate);
        existingCourse.setTeacherId(teacherId);

        // Only update the image if a new one is provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(image);
            existingCourse.setImage(imageUrl);
        }

        // Save the updated course
        Course updatedCourse = courseService.updateCourse(id, existingCourse);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable String id) {
        String response = courseService.deleteCourse(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{studentId}/{courseId}")
    public ResponseEntity<Course> addStudentToCourse(@PathVariable String studentId, @PathVariable String courseId) {
        try {
            Course updatedCourse = courseService.addStudentToCourse(studentId,courseId);
            return ResponseEntity.ok(updatedCourse);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/getAllCourses/{studentId}")
    public ResponseEntity<List<Course>> getStudentCourses(@PathVariable String studentId) {
        List<Course> courses = courseService.getCoursesByStudentId(studentId);
        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/teacher/{teacherId}/{courseId}")
    public ResponseEntity<Course> addTeacherToCourse(@PathVariable String teacherId, @PathVariable String courseId) {

        System.out.println( teacherId + courseId);

        try {
            Course updatedCourse = courseService.addTeacherToCourse(teacherId,courseId);
            return ResponseEntity.ok(updatedCourse);
        } catch (RuntimeException ex) {
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        }
    }

    @GetMapping("/teacher/getAllCourses/{teacherId}")
    public ResponseEntity<List<Course>> getTeacherCourses(@PathVariable String teacherId) {
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(courses);
    }

}
