from pathlib import Path
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

# Helper para añadir etiquetas a las barras en un FacetGrid
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
# profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi,maxKm
outdir = Path("plots/experimento_7")
outdir.mkdir(parents=True, exist_ok=True)

df = pd.read_csv("../data/experimento7.csv")

# Columnas numéricas
cols_numericas = [
    "time_ms", "nodesExpanded", "heurIni", "heurFi", "costIni", "costFi",
    "profitIni", "profitFi", "AssignedPetitionsIni", "AssignedPetitionsFi",
    "trucksUsedIni", "trucksUsedFi", "totalKmIni", "totalKmFi", "maxKm"
]
df[cols_numericas] = df[cols_numericas].apply(pd.to_numeric, errors="coerce")

# === 2. Métricas derivadas ===
df["qualityFi"] = df["profitFi"] - df["costFi"]
df["qualityIni"] = df["profitIni"] - df["costIni"]

# === 3. Visualizaciones ===
sns.set_theme(style="whitegrid")

# --- Bar plots ---
metrics = ["qualityFi", "costFi", "profitFi", "nodesExpanded", "time_ms", "AssignedPetitionsFi"]

# Agrupar por maxKm y mostrar las métricas en función de maxKm
agg = (
    df.groupby(["maxKm"], dropna=False)[metrics]
    .mean()
    .reset_index()
)

x_order = sorted(agg["maxKm"].dropna().unique().tolist())

# Formato de etiqueta por métrica (enteros para contadores)
int_like = {"nodesExpanded", "AssignedPetitionsFi"}
for met in metrics:
    fmt = "%.0f" if met in int_like else "%.2f"

    g = sns.catplot(
        data=agg,
        x="maxKm",
        y=met,
        kind="bar",
        ci=None,
        order=x_order,
        height=4.5,
        aspect=1.3,
        sharey=False
    )
    g.set_axis_labels("maxKm", met)
    g.set_xticklabels(rotation=0)

    # Etiquetas con el valor exacto en cada barra
    add_bar_labels_to_facet(g, fmt=fmt, dy=3, fontsize=8)

    plt.tight_layout()
    g.savefig(outdir / f"bar_{met}_by_maxKm.png", dpi=300)
    plt.close(g.fig)
