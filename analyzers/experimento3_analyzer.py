from pathlib import Path

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

# === 1. Cargar el CSV ===

# --- Configuración ---
outdir = Path("plots/experimento_3")
outdir.mkdir(parents=True, exist_ok=True)

# Header del CSV
# iteration,seed,time_ms,algorithmEI,op1,op2,op3,op4,nodesExpanded,validInicial,validFinal,heurFunction,heurIni,heurFi,costIni,costFi,
# ,profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi,k,lambda
df = pd.read_csv("../data/experimento3.csv")
df_SARecord = pd.read_csv("../data/experimento3_SARecord.csv")

#Header del CSV SARecord
# step,maxIterations,stiter,k,lambda,timestamp_ms,beneficio,coste,peticionesAsignadas,camionesUsados
# Asegúrate de que las columnas numéricas están en el tipo correcto
cols_numericas = ["time_ms", "nodesExpanded", "heurIni", "heurFi", "costIni", "costFi", "profitIni", "profitFi", "AssignedPetitionsIni", "AssignedPetitionsFi", "trucksUsedIni", "trucksUsedFi", "totalKmIni", "totalKmFi", "k", "lambda"]
df[cols_numericas] = df[cols_numericas].apply(pd.to_numeric, errors="coerce")

cols_numericas_sa = [
    "step", "maxIterations", "stiter", "k", "lambda", "timestamp_ms",
    "beneficio", "coste", "peticionesAsignadas", "camionesUsados"
]
df_SARecord[cols_numericas_sa] = df_SARecord[cols_numericas_sa].apply(pd.to_numeric, errors="coerce")

# =========================
# 2) Métricas derivadas
# =========================
# Calidad final e inicial
df["qualityFi"] = df["profitFi"] - df["costFi"]
df["qualityIni"] = df["profitIni"] - df["costIni"]

# Métricas SA (evolución)
df_SARecord["calidad"] = df_SARecord["beneficio"] - df_SARecord["coste"]

# =========================
# 3) Parámetros escalables
# =========================
# (A) Métricas agregadas para barras 3D (media por (k, lambda))
METRICAS_AGREGADAS = [
    "qualityFi", "heurFi", "costFi", "profitFi",
    "nodesExpanded", "time_ms", "AssignedPetitionsFi"
]

# (B) Métricas de evolución por step en SARecord
METRICAS_SA_EVOLUCION = [
    "calidad"
]

# =========================
# 4) Helper para guardar figuras
# =========================
def guardar_fig(ruta: Path):
    ruta.parent.mkdir(parents=True, exist_ok=True)
    plt.tight_layout()
    plt.savefig(ruta, dpi=300)
    plt.close()

def annotate_bar_values(ax, bars, fmt="{:.2f}", fontsize=9, color="black", rotation=0):
    """Annotate 2D bar containers (matplotlib.axes.Axes.bar result)."""
    for bar in bars:
        height = bar.get_height()
        ax.text(
            bar.get_x() + bar.get_width() / 2,
            height,
            fmt.format(height),
            ha="center",
            va="bottom",
            fontsize=fontsize,
            color=color,
            rotation=rotation
        )

# =========================
# 5) Barras 3D: media(métrica) por (k, lambda)
# =========================
# Nota: eje X = k, eje Z = lambda, eje Y = valor medio de la métrica.
# Si una métrica nueva se añade a METRICAS_AGREGADAS, se graficará sola.
agrupado = (
    df
    .groupby(["k", "lambda"], dropna=True)[METRICAS_AGREGADAS]
    .mean()
    .reset_index()
)

# Valores únicos ordenados para posicionar barras
ks = np.sort(agrupado["k"].dropna().unique())
lambdas = np.sort(agrupado["lambda"].dropna().unique())

# Mapas índice -> valor para ubicar las barras en una malla regular
k_to_ix = {k: i for i, k in enumerate(ks)}
lam_to_ix = {l: i for i, l in enumerate(lambdas)}

# Anchuras de barra (constantes para simplicidad)
dx = dy = 0.4

for metrica in METRICAS_AGREGADAS:
    # Construimos listas de posiciones y alturas
    xs, ys, zs = [], [], []
    for _, row in agrupado[["k", "lambda", metrica]].dropna().iterrows():
        xs.append(k_to_ix[row["k"]])
        ys.append(lam_to_ix[row["lambda"]])
        zs.append(row[metrica])

    if not xs:
        continue  # No hay datos para esta métrica

    fig = plt.figure(figsize=(11, 7))
    ax = fig.add_subplot(111, projection="3d")

    # Colors
    xs_arr = np.array(xs, dtype=float)
    unique_xs, inverse = np.unique(xs_arr, return_inverse=True)
    n_unique = len(unique_xs)

    if n_unique == 1:
        # single unique height -> all bars same color
        cmap = plt.get_cmap("viridis", 1)
        color_map = np.tile(cmap(0), (len(xs_arr), 1))
    else:
        # preferred modern API; fallback if the lut argument is unsupported
        try:
            cmap = plt.get_cmap("viridis", n_unique)
        except TypeError:
            cmap = plt.get_cmap("viridis")
        distinct_colors = cmap(np.linspace(0, 1, n_unique))
        color_map = distinct_colors[inverse]


    # Las barras 3D en matplotlib usan (x, y, z, dx, dy, dz)
    ax.bar3d(
        np.array(xs),           # eje X en índices (k)
        np.array(ys),           # eje Y interno (usaremos como Z lógica)
        np.zeros_like(zs),      # base en z = 0
        dx, dy, np.array(zs),   # anchuras y altura
        shade=True,
        color=color_map
    )

    # Etiquetas: mostramos los valores reales en los ejes
    ax.set_xlabel("k (índice)")
    ax.set_ylabel("lambda (índice)")
    ax.set_zlabel(f"media {metrica}")

    # Ticks: convertir índices a valores reales de k y lambda
    ax.set_xticks(np.arange(len(ks)))
    ax.set_yticks(np.arange(len(lambdas)))
    ax.set_xticklabels([str(k) for k in ks], rotation=0)
    ax.set_yticklabels([str(l) for l in lambdas], rotation=0)

    ax.set_title(f"Media de {metrica} por (k, lambda)")

    guardar_fig(outdir / f"bar3d_mean_{metrica}.png")

# =========================
# 6) Evolución SA: step vs métrica
# =========================
# Para cada (k, lambda) generamos una figura por métrica, trazando una línea
# por cada combinación (maxIterations, stiter) con su etiqueta.
if not df_SARecord.empty:
    # Limpiar posibles NaN en step y métricas
    df_SARecord = df_SARecord.dropna(subset=["step"])

    pares_kl = (
        df_SARecord[["k", "lambda"]]
        .dropna()
        .drop_duplicates()
        .sort_values(["k", "lambda"])
        .itertuples(index=False, name=None)
    )

    for k_val, lam_val in pares_kl:
        df_kl = df_SARecord[(df_SARecord["k"] == k_val) & (df_SARecord["lambda"] == lam_val)]
        if df_kl.empty:
            continue

        # Grupos por configuración de SA (para diferenciar líneas)
        grupos = df_kl.groupby(["maxIterations", "stiter"], dropna=True)

        for metrica in METRICAS_SA_EVOLUCION:
            plt.figure(figsize=(10, 6))

            # Dibujar una línea por cada (maxIterations, stiter)
            for (mi, st), g in grupos:
                g_ord = g.sort_values("step")
                plt.plot(
                    g_ord["step"],
                    g_ord[metrica],
                    linewidth=1.5,
                    label=f"maxIter={int(mi)} | stiter={int(st)}"
                )

            plt.title(f"Evolución de {metrica} | k={k_val}, lambda={lam_val}")
            plt.xlabel("step")
            plt.ylabel(metrica)
            plt.legend(title="Config SA", fontsize=9)
            plt.grid(True, linestyle="--", alpha=0.4)

            guardar_fig(outdir / f"SARecord_{metrica}_k{k_val}_lambda{lam_val}.png")

# =========================
# 5.a) Subplot 2x2: calidad (qualityFi) por lambda para cada k
# =========================
# Toma hasta 4 valores de k (si hay menos, se adapta)
ks_for_grid = ks[:4]
n_k = len(ks_for_grid)
n_rows, n_cols = (2, 2) if n_k > 2 else (1, n_k)

fig, axes = plt.subplots(n_rows, n_cols, figsize=(12, 8), squeeze=False)

# Escala común del eje Y para facilitar comparación entre subplots
ymax = (
    agrupado
    .query("k in @ks_for_grid")[["k", "lambda", "qualityFi"]]
    .dropna()["qualityFi"]
    .max()
)
ymin = (
    agrupado
    .query("k in @ks_for_grid")[["k", "lambda", "qualityFi"]]
    .dropna()["qualityFi"]
    .min()
)
# Deja un margen visual
if pd.notna(ymax) and pd.notna(ymin):
    pad = 0.05 * (ymax - ymin if ymax != ymin else (abs(ymax) + 1))
    ymin_plot, ymax_plot = ymin - pad, ymax + pad
else:
    ymin_plot, ymax_plot = None, None

for idx, k_val in enumerate(ks_for_grid):
    r, c = divmod(idx, n_cols)
    ax_sub = axes[r][c]

    df_k = (
        agrupado.loc[agrupado["k"] == k_val, ["lambda", "qualityFi"]]
        .dropna()
        .sort_values("lambda")
    )

    # Si no hay datos, deja aviso en el subplot
    if df_k.empty:
        ax_sub.text(0.5, 0.5, "Sin datos", ha="center", va="center")
        ax_sub.set_axis_off()
        continue

    # Barras: eje X = lambdas (como texto), Y = qualityFi media
    xs = np.arange(len(df_k))
    bars = ax_sub.bar(xs, df_k["qualityFi"].to_numpy())
    annotate_bar_values(ax_sub, bars, fmt="{:.2f}")

    ax_sub.set_xticks(xs)
    ax_sub.set_xticklabels([str(l) for l in df_k["lambda"].to_numpy()], rotation=0)
    ax_sub.set_title(f"k = {k_val}")
    ax_sub.set_xlabel("lambda")
    ax_sub.set_ylabel("qualityFi (media)")

    if ymin_plot is not None and ymax_plot is not None:
        ax_sub.set_ylim(ymin_plot, ymax_plot)

# Quita ejes vacíos si hay menos de 4 k
total_axes = n_rows * n_cols
for j in range(n_k, total_axes):
    r, c = divmod(j, n_cols)
    axes[r][c].set_axis_off()

plt.suptitle("Quality (qualityFi) por lambda • una gráfica por k", y=0.98)
plt.tight_layout()
guardar_fig(outdir / "bar_quality_by_lambda_per_k.png")

# =========================
# 7) Mensaje final útil
# =========================
print("Gráficas generadas en:", outdir.resolve())