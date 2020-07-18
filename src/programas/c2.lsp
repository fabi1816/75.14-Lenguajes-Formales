
(de C2 (LF X)
    (prin3 (quote uno)) (prin3 (first LF)) (terpri)
    (prin3 (quote dos)) (prin3 ((first LF) X)) (terpri)
    (prin3 (quote tres)) (prin3 (list (rest LF) X)) (terpri)
    (cons ((first LF) X) ()))