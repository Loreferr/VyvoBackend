package com.epicode.Spring.security.service;

import com.epicode.Spring.security.payload.EditProfileDto;
import com.epicode.Spring.security.payload.LoginDto;
import com.epicode.Spring.security.payload.RegisterDto;

public interface AuthService {

	String login(LoginDto loginDto);

	String register(RegisterDto registerDto);

	String editprofile(EditProfileDto editprofileDto);

}
