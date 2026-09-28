package com.enterprise.hr.service;

import com.enterprise.hr.domain.*;
import com.enterprise.hr.dto.OrganizationDtos.*;
import com.enterprise.hr.exception.*;
import com.enterprise.hr.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
public class OrganizationService {
 private final DepartmentRepository departments; private final DesignationRepository designations;
 public OrganizationService(DepartmentRepository d, DesignationRepository g) { departments=d; designations=g; }
 public OrganizationResponse createDepartment(OrganizationRequest r) { if(departments.existsByNameIgnoreCase(r.name())) throw new BadRequestException("Department name already exists"); Department x=new Department(); set(x,r); return department(departments.save(x)); }
 public List<OrganizationResponse> departments() { return departments.findAll().stream().map(this::department).toList(); }
 public OrganizationResponse department(Long id) { return department(departments.findById(id).orElseThrow(()->new NotFoundException("Department not found"))); }
 public OrganizationResponse updateDepartment(Long id, OrganizationRequest r) { Department x=departments.findById(id).orElseThrow(()->new NotFoundException("Department not found")); set(x,r); return department(x); }
 public void deleteDepartment(Long id) { if(!departments.existsById(id)) throw new NotFoundException("Department not found"); departments.deleteById(id); }
 public OrganizationResponse createDesignation(OrganizationRequest r) { if(designations.existsByNameIgnoreCase(r.name())) throw new BadRequestException("Designation name already exists"); Designation x=new Designation(); set(x,r); return designation(designations.save(x)); }
 public List<OrganizationResponse> designations() { return designations.findAll().stream().map(this::designation).toList(); }
 public OrganizationResponse designation(Long id) { return designation(designations.findById(id).orElseThrow(()->new NotFoundException("Designation not found"))); }
 public OrganizationResponse updateDesignation(Long id, OrganizationRequest r) { Designation x=designations.findById(id).orElseThrow(()->new NotFoundException("Designation not found")); set(x,r); return designation(x); }
 public void deleteDesignation(Long id) { if(!designations.existsById(id)) throw new NotFoundException("Designation not found"); designations.deleteById(id); }
 private void set(Department x, OrganizationRequest r) { x.setName(r.name().trim()); x.setDescription(r.description()); } private void set(Designation x, OrganizationRequest r) { x.setName(r.name().trim()); x.setDescription(r.description()); }
 private OrganizationResponse department(Department x) { return new OrganizationResponse(x.getId(),x.getName(),x.getDescription()); } private OrganizationResponse designation(Designation x) { return new OrganizationResponse(x.getId(),x.getName(),x.getDescription()); }
}
