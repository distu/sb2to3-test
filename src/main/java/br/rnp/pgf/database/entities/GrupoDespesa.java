package br.rnp.pgf.database.entities;

import br.rnp.pgf.cli.annotations.EnableCsv;
import br.rnp.pgf.cli.annotations.MainDescription;
import br.rnp.pgf.cli.annotations.Searchable;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tb_grupo_despesa")
@EnableCsv
public class GrupoDespesa implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "co_grupo_despesa_id")
    private Long id;

    @MainDescription(value = "", dropdown = false)
    @Searchable
    @Column(name = "ds_descricao")
    private String descricao;

    @OneToMany(mappedBy = "grupoDespesa")
    private List<NotaFiscal> notaFiscal;

    @Searchable
    @Column(name = "ds_codigo_contabil")
    private String codigoContabil;

    @Column(name = "hr_created_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "hr_updated_date", nullable = false, updatable = true)
    @LastModifiedDate
    private LocalDateTime updatedDate;

    public GrupoDespesa(Long id, String descricao, List<NotaFiscal> notaFiscal, String codigoContabil) {
        this.id = id;
        this.descricao = descricao;
        this.notaFiscal = notaFiscal;
        this.codigoContabil = codigoContabil;
    }
}