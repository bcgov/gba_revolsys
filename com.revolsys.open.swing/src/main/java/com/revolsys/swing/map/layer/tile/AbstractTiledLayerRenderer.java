package com.revolsys.swing.map.layer.tile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.revolsys.geometry.model.BoundingBox;
import com.revolsys.geometry.model.GeometryFactory;
import com.revolsys.swing.map.layer.AbstractLayerRenderer;
import com.revolsys.swing.map.layer.Layer;
import com.revolsys.swing.map.view.ViewRenderer;
import com.revolsys.swing.parallel.RunnableSwingWorkerManager;
import com.revolsys.util.BooleanCancellable;
import com.revolsys.util.Cancellable;
import com.revolsys.util.Property;

public abstract class AbstractTiledLayerRenderer<D, T extends AbstractMapTile<D>>
  extends AbstractLayerRenderer<AbstractTiledLayer<D, T>> implements PropertyChangeListener {

  public static final String TILES_LOADED = "loading";

  private static RunnableSwingWorkerManager tileLoaderManager = new RunnableSwingWorkerManager(
    "Load Map Tiles");

  private final Map<T, T> cachedTiles = new HashMap<>();

  private BooleanCancellable cancellable = new BooleanCancellable();

  private GeometryFactory geometryFactory;

  private final List<Runnable> loadingTasks = new ArrayList<>();

  private double resolution;

  public AbstractTiledLayerRenderer(final String type, final AbstractTiledLayer<D, T> layer) {
    super(type, layer);
    Property.addListener(layer, this);
  }

  public AbstractTiledLayerRenderer(final String type, final String name) {
    super(type, name);
  }

  protected void clearCachedTiles() {
    synchronized (this.cachedTiles) {
      this.cachedTiles.clear();
      tileLoaderManager.removeTasks(this.loadingTasks);
      this.loadingTasks.clear();
      this.cancellable.cancel();
      this.cancellable = new BooleanCancellable();
    }
  }

  public T getCachedTile(final T mapTile) {
    return this.cachedTiles.get(mapTile);
  }

  public double getResolution() {
    return this.resolution;
  }

  @Override
  public void propertyChange(final PropertyChangeEvent event) {
    final Object newValue = event.getNewValue();
    if (newValue instanceof BoundingBox) {
      final BoundingBox newBoundingBox = (BoundingBox)newValue;
      synchronized (this.cachedTiles) {
        final List<T> mapTiles = new ArrayList<>(this.cachedTiles.keySet());
        final GeometryFactory newGeometryFactory = newBoundingBox.getGeometryFactory();
        for (final T mapTile : mapTiles) {
          final BoundingBox boundingBox = mapTile.getBoundingBox();
          final GeometryFactory geometryFactory = boundingBox.getGeometryFactory();
          if (!geometryFactory.equals(newGeometryFactory)
            || !newBoundingBox.bboxIntersects(boundingBox)) {
            this.cachedTiles.remove(mapTile);
          }
        }
      }
    } else if (!TILES_LOADED.equals(event.getPropertyName())) {
      clearCachedTiles();
    }
    if (!(event.getSource() instanceof Layer)) {
      firePropertyChange(event);
    }
  }

  @Override
  public void render(final ViewRenderer view, final AbstractTiledLayer<D, T> layer) {
    final GeometryFactory viewportGeometryFactory = view.getGeometryFactory();
    final double layerResolution = layer.getResolution(view);
    synchronized (this.cachedTiles) {
      if (layerResolution != this.resolution || viewportGeometryFactory != this.geometryFactory) {
        this.resolution = layerResolution;
        this.geometryFactory = viewportGeometryFactory;
        clearCachedTiles();
      }
    }
    final List<Runnable> tasks = new ArrayList<>();
    final List<T> mapTiles = layer.getOverlappingMapTiles(view);
    final BooleanCancellable cancellable = this.cancellable;
    for (final ListIterator<T> iterator = mapTiles.listIterator(); !cancellable.isCancelled()
      && iterator.hasNext();) {
      final T mapTile = iterator.next();
      synchronized (this.cachedTiles) {
        T cachedTile = getCachedTile(mapTile);
        if (cachedTile == null) {
          cachedTile = mapTile;
          this.cachedTiles.put(cachedTile, cachedTile);
          final Runnable task = new TileLoadTask<>(this, cancellable, viewportGeometryFactory,
            cachedTile);
          tasks.add(task);
        }
        iterator.set(cachedTile);
      }
    }
    renderTiles(view, cancellable, mapTiles);
    synchronized (this.loadingTasks) {
      this.loadingTasks.addAll(tasks);
      tileLoaderManager.setDescription("Load tiles: " + layer.getPath());
      tileLoaderManager.addTasks(tasks);
    }
  }

  protected abstract void renderTile(final ViewRenderer view, final Cancellable cancellable,
    final T tile);

  protected void renderTiles(final ViewRenderer view, final BooleanCancellable cancellable,
    final List<T> mapTiles) {
    for (final T mapTile : cancellable.cancellable(mapTiles)) {
      renderTile(view, cancellable, mapTile);
    }
  }

  public void setLoaded(final TileLoadTask<D, T> tileLoadTask) {
    this.loadingTasks.remove(tileLoadTask);
    final AbstractTiledLayer<D, T> layer = getLayer();
    if (layer != null) {
      layer.firePropertyChange(TILES_LOADED, false, true);
    }
  }

}