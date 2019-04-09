# Spring Boot + Firebase

## Intro
In this project Firebase is integreted into the backend. It helps clients to authenticate firebase and call backend REST APIs.
Also this included Spring Boot Application Server that can make message and push notification to  Client via Firebase.


## Firebase integration

This project integreted Firebase with following steps
* Maven Dependencies(Add firebase client SDK)
* Filter
* Authentication provider
* Token parsing
* Registration
* Implemnting Firebase Cloud Messaging(FCM)

Firebase can be enabled by setting 
```
rs.pscode.firebase.enabled=true/false
```
in application.properties. This will register especial FirebaseFilter and authentication provider.

### Dependencies 
```
<dependency>
	<groupId>com.google.firebase</groupId>
	<artifactId>firebase-server-sdk</artifactId>
	<version>3.0.1</version>
</dependency>
```
### Firebase filter

Filter checks if  header named "X-Authorization-Firebase" exists, if does not this means that request should not be processed by this filter. When token is found it is parsed using ```firebaseService.parseToken(xAuth)``` and correct Authentication is created ```FirebaseAuthenticationToken``` that is put in the securityContextHolder. Job of the filter is here done and authentication process is being taken over by authentication provider.


```
public class FirebaseFilter extends OncePerRequestFilter {

	private static String HEADER_NAME = "X-Authorization-Firebase";

	private FirebaseService firebaseService;

	public FirebaseFilter(FirebaseService firebaseService) {
		this.firebaseService = firebaseService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String xAuth = request.getHeader(HEADER_NAME);
		if (StringUtil.isBlank(xAuth)) {
			filterChain.doFilter(request, response);
			return;
		} else {
			try {
				FirebaseTokenHolder holder = firebaseService.parseToken(xAuth);

				String userName = holder.getUid();

				Authentication auth = new FirebaseAuthenticationToken(userName, holder);
				SecurityContextHolder.getContext().setAuthentication(auth);

				filterChain.doFilter(request, response);
			} catch (FirebaseTokenInvalidException e) {
				throw new SecurityException(e);
			}
		}
	}

}
```
### Authentication provider
Authentication provider is made so that we map the firebase user to our database. Provider only supports FirebaseAuthenticationToken.

```
@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	@Qualifier(value = UserServiceImpl.NAME)
	private UserDetailsService userService;

	public boolean supports(Class<?> authentication) {
		return (FirebaseAuthenticationToken.class.isAssignableFrom(authentication));
	}

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!supports(authentication.getClass())) {
			return null;
		}

		FirebaseAuthenticationToken authenticationToken = (FirebaseAuthenticationToken) authentication;
		UserDetails details = userService.loadUserByUsername(authenticationToken.getName());
		if (details == null) {
			throw new FirebaseUserNotExistsException();
		}

		authenticationToken = new FirebaseAuthenticationToken(details, authentication.getCredentials(),
				details.getAuthorities());

		return authenticationToken;
	}

}

```

## Firebase Token parser
Firebase parse validates the token and returns instance of especial class FirebaseTokenHolder.

```
public class FirebaseParser {
	public FirebaseTokenHolder parseToken(String idToken) {
		if (StringUtil.isBlank(idToken)) {
			throw new IllegalArgumentException("FirebaseTokenBlank");
		}
		try {
			Task<FirebaseToken> authTask = FirebaseAuth.getInstance().verifyIdToken(idToken);

			Tasks.await(authTask);

			return new FirebaseTokenHolder(authTask.getResult());
		} catch (Exception e) {
			throw new FirebaseTokenInvalidException(e.getMessage());
		}
	}
```

One of the key parts of the firebase integration is the application initialization. To complete the initialization you must downlod json from firebase console , check this link https://firebase.google.com/docs/server/setup .

```
@Configuration
public class FirebaseConfig {

	@Bean
	public DatabaseReference firebaseDatabse() {
		DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
		return firebase;
	}

	@Value("${rs.pscode.firebase.database.url}")
	private String databaseUrl;

	@Value("${rs.pscode.firebase.config.path}")
	private String configPath;

	@PostConstruct
	public void init() {

		/**
		 * https://firebase.google.com/docs/server/setup
		 * 
		 * Create service account , download json
		 */
		InputStream inputStream = FirebaseConfig.class.getClassLoader().getResourceAsStream(configPath);

		FirebaseOptions options = new FirebaseOptions.Builder().setServiceAccount(inputStream)
				.setDatabaseUrl(databaseUrl).build();
		FirebaseApp.initializeApp(options);
		
	}
}

```
