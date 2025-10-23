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
# ,profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi,totalTrucks,totalGasStations
outdir = Path("plots/experimento_4")
outdir.mkdir(parents=True, exist_ok=True)

df = pd.read_csv("../data/experimento4.csv")

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
metrics = ["time_ms", "qualityFi", "nodesExpanded", "AssignedPetitionsFi"]

#En este ejercicio basicamente tengo que comparar el rendimiento de los algoritmos variando el numero de camiones y gasolineras

# Agrupar por totalTrucks y totalGasStations, y luego hacer un barplot para cada métrica agrupando también por algorithmSearch
agg = (
    df.groupby(["totalTrucks", "totalGasStations", "algorithmSearch"], dropna=False)[metrics]
    .mean()
    .reset_index()
)

# === Comparación directa en un único plot (ambos algorithmSearch) agregando por totalGasStations ===
agg_all = (
    df.groupby(["totalTrucks", "algorithmSearch"], dropna=False)[metrics]
    .mean()                      # promedio sobre totalGasStations (y el resto de repeticiones)
    .reset_index()
)

# Orden limpio del eje X
x_order = sorted(agg_all["totalTrucks"].dropna().unique().tolist())

for met in metrics:
    g = sns.catplot(
        data=agg_all,
        x="totalTrucks",
        y=met,
        hue="algorithmSearch",
        kind="bar",
        ci=None,
        order=x_order,
        height=4.5,
        aspect=1.3,
        sharey=False
    )
    g.set_axis_labels("totalTrucks", met)
    if g._legend is not None:
        g._legend.set_title("algorithmSearch")

    # Etiquetas de valor encima de cada barra
    fmt = "%.0f" if met in {"AssignedPetitionsFi"} else "%.2f"
    add_bar_labels_to_facet(g, fmt=fmt, dy=3, fontsize=8)

    plt.tight_layout()
    g.savefig(outdir / f"bar_{met}_by_trucks_agg_over_gasstations_hue_algo.png", dpi=300)
    plt.close(g.fig)

