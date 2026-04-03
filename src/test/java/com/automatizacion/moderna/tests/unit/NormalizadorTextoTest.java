package com.automatizacion.moderna.tests.unit;
import com.automatizacion.moderna.utils.NormalizadorTexto;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class NormalizadorTextoTest {
    @Test
    void debeResolverEscapesYRemoverSaltoFinal() {
        String esperado = "This text is stored in a raw Asset.\\n\\nIt was read and placed into the TextView here.\\n";
        String normalizado = NormalizadorTexto.paraComparacion(esperado);
        assertThat(normalizado)
            .isEqualTo("This text is stored in a raw Asset.\n\nIt was read and placed into the TextView here.");
    }
    @Test
    void debePermitirComparacionFlexibleConSaltosYEspacios() {
        String esperadoFeature = "This text is stored in a raw Asset. It was read and placed into the TextView here.";
        String actualUi = "This text is stored in a raw Asset.\n\nIt was read and placed into the TextView here.";
        String esperadoNormalizado = NormalizadorTexto.paraComparacionFlexible(esperadoFeature);
        String actualNormalizado = NormalizadorTexto.paraComparacionFlexible(actualUi);
        assertThat(actualNormalizado).contains(esperadoNormalizado);
    }
}
