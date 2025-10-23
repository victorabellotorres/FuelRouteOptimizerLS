from pathlib import Path
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

def add_bar_labels_to_facet(g, fmt="%.2f", dy=3, fontsize=8):
    # Recorre todos los ejes del FacetGrid y etiqueta cada barra
    for ax in g.axes.flat:
        # Si no hay barras, sigue
        if not hasattr(ax, "patches"):
            continue
        # Asegura margen vertical para que quepan las etiquetas
        ax.margins(y=0.10)
        for p in ax.patches:
            h = p.get_height()
            if h is None or np.isnan(h):
                continue
            x = p.get_x() + p.get_width() / 2
            ax.annotate(fmt % h,
                        xy=(x, h),
                        xytext=(0, dy), textcoords="offset points",
                        ha="center", va="bottom",
                        fontsize=fontsize)


# === 1. Cargar el CSV ===
# Header:
# iteration,seed,time_ms,algorithmEI,algorithmSearch,op1,op2,op3,op4,nodesExpanded,validInicial,validFinal,heurFunction,heurIni,heurFi,costIni,costFi,
# profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi,centrosDist,camionesPorCentros
outdir = Path("plots/experimento_5")
outdir.mkdir(parents=True, exist_ok=True)

df = pd.read_csv("../data/experimento5.csv")

# Columnas numéricas
cols_numericas = [
    "time_ms", "nodesExpanded", "heurIni", "heurFi", "costIni", "costFi",
    "profitIni", "profitFi", "AssignedPetitionsIni", "AssignedPetitionsFi",
    "trucksUsedIni", "trucksUsedFi", "totalKmIni", "totalKmFi"
]
df[cols_numericas] = df[cols_numericas].apply(pd.to_numeric, errors="coerce")

# === 2. Métricas derivadas ===
df["qualityFi"] = df["profitFi"] - df["costFi"]
df["qualityIni"] = df["profitIni"] - df["costIni"]

# === 3. Visualizaciones ===
sns.set_theme(style="whitegrid")

# --- Bar plots ---
metrics = ["totalKmFi", "qualityFi", "profitFi", "costFi", "AssignedPetitionsFi"]


# Cada categoría del eje X es la pareja (centrosDist, camionesPorCentros)
agg_pairs = (
    df.groupby(["centrosDist", "camionesPorCentros"], dropna=False)[metrics]
    .mean()
    .reset_index()
    .sort_values(["centrosDist", "camionesPorCentros"])
)

# Etiqueta legible para el eje X
def _fmt_pair(row):
    c = row["centrosDist"]
    t = row["camionesPorCentros"]
    c_str = f"{int(c)}" if pd.notna(c) and float(c).is_integer() else f"{c}"
    t_str = f"{int(t)}" if pd.notna(t) and float(t).is_integer() else f"{t}"
    return f"centros={c_str} | camiones={t_str}"

agg_pairs["grupo"] = agg_pairs.apply(_fmt_pair, axis=1)

x_order = agg_pairs["grupo"].tolist()  # ya viene ordenado por el sort_values

for met in metrics:
    fmt = "%.0f" if met in {"AssignedPetitionsFi", "trucksUsedFi"} else "%.2f"

    g = sns.catplot(
        data=agg_pairs,
        x="grupo",
        y=met,
        kind="bar",
        ci=None,
        order=x_order,
        height=4.8,
        aspect=1.6,
        sharey=False
    )
    g.set_axis_labels("centrosDist | camionesPorCentros", met)
    g.set_xticklabels(rotation=30, ha="right")

    add_bar_labels_to_facet(g, fmt=fmt, dy=3, fontsize=8)
    plt.title(f"{met} por pareja (centrosDist, camionesPorCentros)")
    plt.tight_layout()
    g.savefig(outdir / f"bar_{met}_by_pair_centros_camiones.png", dpi=300)
    plt.close(g.fig)
