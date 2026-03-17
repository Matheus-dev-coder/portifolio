//Sistema bancário
public class Conta {
    private String titular;
    private double saldo;

    public Conta(String titular, double saldoInicial) {
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public void depositar(double valor) {
        saldo += valor;
    }

    public void sacar(double valor) {
        if (valor <= saldo) {
            saldo -= valor;
        } else {
            System.out.println("Saldo insuficiente.");
        }
    }

    public double getSaldo() {
        return saldo;
    }
}

//cadastro de produtos em um sistema de getão.
public class Produto {
    private String nome;
    private double preco;

    public Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public void exibirInformacoes() {
        System.out.println("Produto: " + nome + " | Preço: R$ " + preco);
    }
}

public class Estoque {
    private List<Produto> produtos = new ArrayList<>();

    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
    }

    public void listarProdutos() {
        for (Produto p : produtos) {
            p.exibirInformacoes();
        }
    }
}

public class JwtFiltroAutenticacao extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extrairToken(request);
        if (token != null && validarToken(token)) {
            String email = obterEmailDoToken(token);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
    // métodos auxiliares para extrair, validar e obter dados do token
}

Esse filtro é registrado na configuração de segurança do Spring:
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/login").permitAll()
            .anyRequest().authenticated()
            .and()
 .addFilterBefore(new JwtFiltroAutenticacao(), UsernamePasswordAuthenticationFilter.class);
    }
}

@Entity
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
/** ID gerado automaticamente pelo banco de dados.
 * O GenerationType.IDENTITY informa ao JPA que o próprio banco de dados será responsável
 * por atribuir um valor único ao ID, geralmente através de auto-incremento.
 */
    private Long id;
    private String nome;
    private Double preco;
}

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto salvarProduto(Produto produto) {
        // Exemplo de lógica de negócio: validação simples
        if (produto.getPreco() <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior que zero.");
        }

        // Exemplo adicional: cálculo de imposto antes de salvar
        double imposto = produto.getPreco() * 0.1; // 10% de imposto
        produto.setPreco(produto.getPreco() + imposto);

        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }
}

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Produto> listar() {
        return service.listarTodos();
    }

    @PostMapping
    public Produto criar(@RequestBody Produto produto) {
        return service.salvar(produto);
    }
}
