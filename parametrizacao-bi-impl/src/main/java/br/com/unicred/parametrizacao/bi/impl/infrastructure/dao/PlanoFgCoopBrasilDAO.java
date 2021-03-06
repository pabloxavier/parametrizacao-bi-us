package br.com.unicred.parametrizacao.bi.impl.infrastructure.dao;

import java.sql.Types;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.unicred.parametrizacao.bi.impl.business.domain.PlanoFgCoopBrasil;
import br.com.unicred.parametrizacao.bi.impl.business.exceptions.ErroInesperadoException;
import br.com.unicred.parametrizacao.bi.impl.business.exceptions.NotFoundException;

@Repository
public class PlanoFgCoopBrasilDAO {

    private final JdbcTemplate jdbcTemplate;
    private Logger log = LoggerFactory.getLogger(PlanoFgCoopBrasilDAO.class);
    private static final BeanPropertyRowMapper<PlanoFgCoopBrasil> ROW_MAPPER = BeanPropertyRowMapper.newInstance(PlanoFgCoopBrasil.class);

    private static final String BUSCA_CONTA_BACEN_SQL = "select codigo_conta_bacen, descricao_conta_bacen from edw.plano_fg_coop_brasil";
    private static final String BUSCA_CONTA_BACEN_SQL_BY_CODIGO = "select codigo_conta_bacen, descricao_conta_bacen from edw.plano_fg_coop_brasil WHERE codigo_conta_bacen = ? ";
    private static final String INSERIR_CONTA_BACEN = "insert into edw.plano_fg_coop_brasil (codigo_conta_bacen, descricao_conta_bacen) values(?,?)";
    private static final String ALTERAR_CONTA_BACEN = "update edw.plano_fg_coop_brasil set codigo_conta_bacen = ?,	descricao_conta_bacen = ? where codigo_conta_bacen = ?";
    private static final String EXCLUIR_CONTA_BACEN = "delete from edw.plano_fg_coop_brasil where codigo_conta_bacen = ?";
    
    @Autowired
    public PlanoFgCoopBrasilDAO(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PlanoFgCoopBrasil> buscaContaBacen() {
        try {
            log.info("Procurando contas bacen.");
            return jdbcTemplate.query(BUSCA_CONTA_BACEN_SQL, ROW_MAPPER);
        } catch (final EmptyResultDataAccessException e) {
            log.warn("Não foram encontrados codigos bacen.", e);
            throw new NotFoundException("Não foram encontrados codigos bacen.");
        }            
    }

    public PlanoFgCoopBrasil buscaContaBacenByCodigo(String codigoContaBacen) {
        	log.info(String.format("Procurando contas bacen pelo codigo %s.", codigoContaBacen));
            return jdbcTemplate.queryForObject(BUSCA_CONTA_BACEN_SQL_BY_CODIGO, new Object[] { codigoContaBacen }, ROW_MAPPER);
    }
    
    public PlanoFgCoopBrasil inserirContaBacen(final PlanoFgCoopBrasil planoFgCoopBrasil) {
        
        try {
            log.info(String.format("Inserindo conta bacen %s com a descrição %s.", planoFgCoopBrasil.getCodigoContaBacen(), planoFgCoopBrasil.getDescricaoContaBacen()));
            jdbcTemplate.update(INSERIR_CONTA_BACEN, getParams(planoFgCoopBrasil), getTypes());
            return planoFgCoopBrasil;
        } catch (Exception e) {
            log.error(String.format("Erro ao inserir conta bacen %s com a descrição %s.", planoFgCoopBrasil.getCodigoContaBacen(), planoFgCoopBrasil.getDescricaoContaBacen()) + e);
            throw new ErroInesperadoException();
        }
    }

    public PlanoFgCoopBrasil alterarContaBacen(final PlanoFgCoopBrasil planoFgCoopBrasil, String codigoContaBacen) {
        
        try {
            log.info(String.format("Alterando conta bacen %s com a descrição %s onde ela for igual a %s.", planoFgCoopBrasil.getCodigoContaBacen(), planoFgCoopBrasil.getDescricaoContaBacen(), codigoContaBacen));
            jdbcTemplate.update(ALTERAR_CONTA_BACEN, getParamsAlter(planoFgCoopBrasil, codigoContaBacen), getTypesAlter());
            return planoFgCoopBrasil;
        } catch (Exception e) {
            log.error(String.format("Erro ao alterar conta bacen %s com a descrição %s onde ela for igual a %s.", planoFgCoopBrasil.getCodigoContaBacen(), planoFgCoopBrasil.getDescricaoContaBacen(), codigoContaBacen) + e);
            throw new ErroInesperadoException();
        }
    }

    public void excluirContaBacen(String codigoContaBacen) {
    	
        try {
            log.info(String.format("Excluindo conta bacen %s.", codigoContaBacen));
            jdbcTemplate.update(EXCLUIR_CONTA_BACEN, codigoContaBacen);    
        } catch (Exception e) {
            log.error(String.format("Erro ao excluir conta bacen %s.", codigoContaBacen) + e);
            throw new ErroInesperadoException();
        }
    }

    
    private Object[] getParams(final PlanoFgCoopBrasil planoFgCoopBrasil) {
        return new Object[] { planoFgCoopBrasil.getCodigoContaBacen(), planoFgCoopBrasil.getDescricaoContaBacen() };
    }
    
    private Object[] getParamsAlter(final PlanoFgCoopBrasil planoFgCoopBrasil, final String codigoContaBacen) {
        return new Object[] { planoFgCoopBrasil.getCodigoContaBacen(), planoFgCoopBrasil.getDescricaoContaBacen(), codigoContaBacen };
    }

    private int[] getTypes() {
        return new int[] { Types.VARCHAR, Types.VARCHAR};
    }

    private int[] getTypesAlter() {
        return new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
    }

}

