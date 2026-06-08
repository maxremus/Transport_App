package org.example.transport_saas.repository;

import org.example.transport_saas.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCompanyId(Long companyId);

    @Query("""
        SELECT COALESCE(SUM(e.amount),0)
        FROM Expense e
        WHERE e.company.id = :companyId
    """)
    BigDecimal totalExpenses(Long companyId);
}
