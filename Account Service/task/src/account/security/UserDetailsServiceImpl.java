package account.security;

import account.database.model.RoleEntity;
import account.database.repository.EmployeeRepository;
import account.database.model.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    EmployeeRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        List<EmployeeEntity> users = userRepo.findByEmail(userEmail.toLowerCase());

        if (users == null || users.size() == 0) {
            throw new UsernameNotFoundException("Not found: " + userEmail);
        }

        EmployeeEntity user = users.get(0);
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .disabled(user.isLocked())
                .build();
    }

    private Collection<GrantedAuthority> getAuthorities(EmployeeEntity user){
        Set<RoleEntity> userRoles = user.getRoles();
        Collection<GrantedAuthority> authorities =
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().toUpperCase()))
                        .collect(Collectors.toList());
        return authorities;
    }
}
