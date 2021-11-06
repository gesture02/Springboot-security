package com.cos.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.respository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
	
	
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCrpytBCryptPasswordEncoder;
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	// 함수 종료 시 @AuthenticationPrincipal이 만들어진다
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		// registraionId로 어떤 OAuth로 로그인했는지 확인 가능
		System.out.println("getClientRegistration : " + userRequest.getClientRegistration());
		System.out.println("getAccessToken : " + userRequest.getAccessToken());
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		// userRequest정보 : 구글로그인 버튼 클릭 -> 구글로그인 창 -> 로그인 완료 -> code를 리턴(OAuth-Client라이브러리) -> Access Token 요청
		// userRequest정보로 -> loadUser함수 호출 -> 구글로부터 회원 프로필을 받아줌
		System.out.println("getAttribute : " + oauth2User.getAttributes());
		
		String provider = userRequest.getClientRegistration().getClientId();//google
		String providerId = oauth2User.getAttribute("sub");
		String username = provider+"_"+providerId;	// google_123412345
		String password = bCrpytBCryptPasswordEncoder.encode("getInThere");
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) {
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		}
		
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
