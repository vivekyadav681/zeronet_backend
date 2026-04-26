package com.example.zeronet.services;

import com.example.zeronet.entities.Organization;
import com.example.zeronet.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public Organization createOrganization(Organization organization) {
        log.info("Creating new organization: {}", organization.getOrgName());
        
        if (organization.getRegistrationId() != null && 
            organizationRepository.existsByRegistrationId(organization.getRegistrationId())) {
            throw new RuntimeException("Organization with this registration ID already exists");
        }
        
        return organizationRepository.save(organization);
    }

    public Optional<Organization> getOrganizationById(UUID id) {
        return organizationRepository.findById(id);
    }

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Transactional
    public Organization updateOrganization(UUID id, Organization updateDetails) {
        log.info("Updating organization with ID: {}", id);
        
        Organization existingOrg = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
                
        if (updateDetails.getOrgName() != null) existingOrg.setOrgName(updateDetails.getOrgName());
        if (updateDetails.getOrgType() != null) existingOrg.setOrgType(updateDetails.getOrgType());
        if (updateDetails.getRegistrationId() != null) existingOrg.setRegistrationId(updateDetails.getRegistrationId());
        if (updateDetails.getEmail() != null) existingOrg.setEmail(updateDetails.getEmail());
        if (updateDetails.getPhone() != null) existingOrg.setPhone(updateDetails.getPhone());
        if (updateDetails.getAddress() != null) existingOrg.setAddress(updateDetails.getAddress());
        if (updateDetails.getLicense() != null) existingOrg.setLicense(updateDetails.getLicense());
        if (updateDetails.getContactPerson() != null) existingOrg.setContactPerson(updateDetails.getContactPerson());
        if (updateDetails.getEmergencyContact() != null) existingOrg.setEmergencyContact(updateDetails.getEmergencyContact());
        if (updateDetails.getWebsite() != null) existingOrg.setWebsite(updateDetails.getWebsite());
        if (updateDetails.getStartTime() != null) existingOrg.setStartTime(updateDetails.getStartTime());
        if (updateDetails.getEndTime() != null) existingOrg.setEndTime(updateDetails.getEndTime());
        if (updateDetails.getIs24Hours() != null) existingOrg.setIs24Hours(updateDetails.getIs24Hours());
        if (updateDetails.getTotalFloors() != null) existingOrg.setTotalFloors(updateDetails.getTotalFloors());
        if (updateDetails.getSecurityStaffCount() != null) existingOrg.setSecurityStaffCount(updateDetails.getSecurityStaffCount());
        if (updateDetails.getCity() != null) existingOrg.setCity(updateDetails.getCity());
        if (updateDetails.getState() != null) existingOrg.setState(updateDetails.getState());
        if (updateDetails.getLat() != null) existingOrg.setLat(updateDetails.getLat());
        if (updateDetails.getLng() != null) existingOrg.setLng(updateDetails.getLng());
        if (updateDetails.getRadius() != null) existingOrg.setRadius(updateDetails.getRadius());

        return organizationRepository.save(existingOrg);
    }

    @Transactional
    public void deleteOrganization(UUID id) {
        log.info("Deleting organization with ID: {}", id);
        if (!organizationRepository.existsById(id)) {
            throw new RuntimeException("Organization not found");
        }
        organizationRepository.deleteById(id);
    }
}
