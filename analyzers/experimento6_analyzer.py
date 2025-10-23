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
# profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi,costeKm,pet0DiaAtendidas,pet1DiaAtendidas,pet2DiaAtendidas,pet3DiaAtendidas
outdir = Path("plots/experimento_6")
outdir.mkdir(parents=True, exist_ok=True)

df = pd.read_csv("../data/experimento6.csv")

# Columnas numéricas
cols_numericas = [
    "time_ms", "nodesExpanded", "heurIni", "heurFi", "costIni", "costFi",
    "profitIni", "profitFi", "AssignedPetitionsIni", "AssignedPetitionsFi",
    "trucksUsedIni", "trucksUsedFi", "totalKmIni", "totalKmFi", "pet0DiaAtendidas", "pet1DiaAtendidas", "pet2DiaAtendidas", "pet3DiaAtendidas", "costeKm"
]
df[cols_numericas] = df[cols_numericas].apply(pd.to_numeric, errors="coerce")

# === 2. Métricas derivadas ===
df["qualityFi"] = df["profitFi"] - df["costFi"]
df["qualityIni"] = df["profitIni"] - df["costIni"]

# === 3. Visualizaciones ===
sns.set_theme(style="whitegrid")

# --- Bar plots ---
metrics = ["totalKmFi", "heurFi", "qualityFi", "AssignedPetitionsFi", "trucksUsedFi", "time_ms", "nodesExpanded"]

# Agrupar por costeKm y mostrar las métricas en función de costeKm
agg_metrics = (
    df.groupby(["costeKm"], dropna=False)[metrics]
    .mean()
    .reset_index()
)

x_order = sorted(agg_metrics["costeKm"].dropna().unique().tolist())

for met in metrics:
    fmt = "%.0f" if met in {"AssignedPetitionsFi", "trucksUsedFi"} else "%.2f"

    g = sns.catplot(
        data=agg_metrics,
        x="costeKm",
        y=met,
        kind="bar",
        ci=None,
        order=x_order,
        height=4.5,
        aspect=1.4,
        sharey=False
    )
    g.set_axis_labels("costeKm", met)
    g.set_xticklabels(rotation=0)
    add_bar_labels_to_facet(g, fmt=fmt, dy=3, fontsize=8)

    plt.tight_layout()
    plt.title(f"{met} vs costeKm (media)")
    g.savefig(outdir / f"bar_{met}_by_costeKm.png", dpi=300)
    plt.close(g.fig)

#=================================================================0

ys = ["pet0DiaAtendidas", "pet1DiaAtendidas", "pet2DiaAtendidas", "pet3DiaAtendidas"]
# Agrupar por cada costeKm y la media de cada variable de ys; visualizar en lineplot
agg_ys = (
    df.groupby(["costeKm"], dropna=False)[ys]
    .mean()
    .reset_index()
)

# Pasar a formato largo para un único gráfico con varias líneas
ys_long = agg_ys.melt(id_vars="costeKm", value_vars=ys,
                      var_name="petDia", value_name="media_atendidas")

# Orden por si costeKm es numérico
x_order = sorted(ys_long["costeKm"].dropna().unique().tolist())

plt.figure(figsize=(8, 5))
sns.lineplot(
    data=ys_long.sort_values(["costeKm", "petDia"]),
    x="costeKm", y="media_atendidas", hue="petDia",
    estimator=None, ci=None
)
plt.title("Peticiones atendidas por día vs costeKm (media)")
plt.xlabel("costeKm")
plt.ylabel("media atendidas")
plt.legend(title="petDia")
plt.grid(True, linestyle="--", alpha=0.4)
plt.tight_layout()
plt.title("Peticiones atendidas por días pendientes vs costeKm (media)")
plt.savefig(outdir / "line_petDias_by_costeKm.png", dpi=300)
plt.close()
